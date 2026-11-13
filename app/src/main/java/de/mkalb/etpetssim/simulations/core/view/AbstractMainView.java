package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.EdgeBehavior;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.model.AbstractSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.shared.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStepEvent;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import de.mkalb.etpetssim.simulations.core.viewmodel.MainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import de.mkalb.etpetssim.ui.GridGeometry;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Abstract base of the main views of the regular 2D simulations.
 *
 * <p>Composes the config, control, and observation regions around the simulation canvas, binds
 * {@link MainViewModel}, and provides drawing and throttling support.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GC>  grid cell type used for cell selection
 * @param <GM>  grid model type managed by the simulation
 * @param <CON> immutable simulation config type
 * @param <STA> statistics type
 * @param <SM>  concrete simulation manager type
 * @param <CTX> simulation-specific user action context type
 * @param <OV>  observation view type
 */
public abstract class AbstractMainView<
        ENT extends GridEntity,
        GC extends GridCellView<ENT>,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends SimulationStatistics,
        SM extends AbstractSimulationManager<ENT, GM, CON, STA>,
        CTX extends SimulationUserActionContext,
        OV extends AbstractObservationView<ENT, GC, STA>>
        implements SimulationMainView {

    protected static final double NO_STROKE_LINE_WIDTH = 0.0d;

    private static final double INITIAL_CANVAS_WIDTH = 640.0d;
    private static final double INITIAL_CANVAS_HEIGHT = 480.0d;
    private static final double MAX_CANVAS_WIDTH = 6_400.0d;
    private static final double MAX_CANVAS_HEIGHT = 4_800.0d;
    private static final String MAIN_FONT_FAMILY = "Verdana";
    private static final String FALLBACK_FONT_FAMILY = "System";
    private static final double MIN_CELL_FONT_SIZE = 7.0d;
    private static final double MIN_EMOJI_FONT_SIZE = 3.0d;
    private static final double CENTER_SPLIT_PANE_DIVIDER_POSITION = 0.65d;
    private static final Color SKIP_OVERLAY_TEXT_COLOR = FXPaintFactory.adjustColorAlpha(FXPaintFactory.BORDER_COLOR,
            0.95);
    private static final Color SKIP_OVERLAY_BACKGROUND_COLOR = FXPaintFactory
            .adjustColorAlpha(FXPaintFactory.BACKGROUND_COLOR, 0.95);
    private static final String SKIP_OVERLAY_SYMBOL = "⏳"; // Hourglass Not Done
    private static final double SKIP_FONT_SIZE = 48.0d;
    private static final double SKIP_PADDING = 24.0d;
    private static final int DRAW_THROTTLER_HISTORY_SIZE = 3;
    private static final int DRAW_THROTTLER_MAX_SKIPS = 4;
    /**
     * Only used for debugging purposes to log draw calls and performance.
     */
    private static final boolean DEBUG_MODE = false;
    private static final String LOG_COMPONENT = "MainView";

    protected final MainViewModel<ENT, GC, GM, CON, STA, SM, CTX> viewModel;
    protected final OV observationView;
    protected final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final AbstractConfigView<CON, ?> configView;
    private final ControlView controlView;
    private final Canvas baseCanvas;
    private final Canvas dynamicCanvas;
    private final Canvas overlayCanvas;
    private final ScrollPane canvasScrollPane;
    private final BorderPane canvasBorderPane;
    private final Label notificationLabel;
    private final EditToolBarView<CTX> editToolBar = new EditToolBarView<>();
    private final Map<Double, Font> fontCache;
    private final DrawCallThrottler drawThrottler = new DrawCallThrottler(DRAW_THROTTLER_HISTORY_SIZE,
            DRAW_THROTTLER_MAX_SKIPS);
    protected @Nullable Font cellFont;
    protected @Nullable Font cellEmojiFont;
    private @Nullable CanvasPainters canvasPainters;
    private @Nullable ChangeListener<SimulationNotificationType> notificationListener;
    private @Nullable ChangeListener<@Nullable GC> selectedGridCellListener;
    private @Nullable Region mainRegion;
    private int lastDrawnStepCount = Integer.MIN_VALUE;
    private boolean skipOverlayActive;

    protected AbstractMainView(MainViewModel<ENT, GC, GM, CON, STA, SM, CTX> viewModel,
                               AbstractConfigView<CON, ?> configView, ControlView controlView,
                               OV observationView,
                               GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.configView = configView;
        this.controlView = controlView;
        this.observationView = observationView;
        this.entityDescriptorRegistry = entityDescriptorRegistry;

        baseCanvas = new Canvas(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT);
        dynamicCanvas = new Canvas(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT);
        overlayCanvas = new Canvas(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT);
        baseCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        dynamicCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        overlayCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);

        canvasBorderPane = new BorderPane();
        canvasBorderPane.getStyleClass().add(FXStyleClasses.SIMULATION_CENTER_BORDERPANE);

        canvasScrollPane = new ScrollPane();
        canvasScrollPane.getStyleClass().add(FXStyleClasses.SIMULATION_SCROLLPANE);

        fontCache = new HashMap<>();

        notificationLabel = new Label();
        notificationLabel.getStyleClass().add(FXStyleClasses.SIMULATION_NOTIFICATION_LABEL);
        clearNotification();
    }

    protected static double computeStrokeLineWidth(CellDimension cellDimension) {
        if (cellDimension.innerRadius() < 2.0d) {
            return NO_STROKE_LINE_WIDTH;
        }
        return Math.log(cellDimension.innerRadius());
    }

    private static double computeCellFontSize(CellDimension cellDimension, CellShape cellShape) {
        return Math.round(cellDimension.height() *
                switch (cellShape) {
                    case TRIANGLE -> 0.135d;
                    case SQUARE -> 0.18d;
                    case HEXAGON -> 0.175d;
                });
    }

    private static double computeCellEmojiFontSize(CellDimension cellDimension, CellShape cellShape) {
        return Math.round(cellDimension.height() *
                switch (cellShape) {
                    case TRIANGLE -> 0.6d;
                    case SQUARE -> 0.9d;
                    case HEXAGON -> 0.8d;
                });
    }

    @Override
    public final Region buildMainRegion() {
        Region cachedMainRegion = mainRegion;
        if (cachedMainRegion != null) {
            return cachedMainRegion;
        }

        Region configRegion = configView.buildConfigRegion();
        Region controlRegion = controlView.buildControlRegion();
        Region observationRegion = observationView.buildObservationRegion();
        Region simulationRegion = createSimulationRegion();

        SplitPane centerSplitPane = new SplitPane();
        centerSplitPane.setOrientation(Orientation.HORIZONTAL);
        centerSplitPane.getStyleClass().add(FXStyleClasses.CENTER_SPLITPANE);
        centerSplitPane.getItems().addAll(simulationRegion, observationRegion);
        centerSplitPane.setDividerPositions(CENTER_SPLIT_PANE_DIVIDER_POSITION);
        SplitPane.setResizableWithParent(observationRegion, false);

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FXStyleClasses.MAIN_BORDERPANE);

        borderPane.setTop(configRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setCenter(centerSplitPane);

        registerViewModelListeners();
        registerNotificationListener();
        registerOverlayPrimaryClickHandler();

        mainRegion = borderPane;
        return borderPane;
    }

    @Override
    public SimulationTerminationHandle shutdownSimulation() {
        if (selectedGridCellListener != null) {
            viewModel.selectedGridCellProperty().removeListener(selectedGridCellListener);
            selectedGridCellListener = null;
        }
        if (notificationListener != null) {
            viewModel.notificationTypeProperty().removeListener(notificationListener);
            notificationListener = null;
        }
        observationView.shutdownObservation();
        SimulationTerminationHandle terminationHandle = viewModel.shutdownSimulation();
        canvasPainters = null;
        cellFont = null;
        cellEmojiFont = null;
        clearNotification();
        editToolBar.clear();
        return terminationHandle;
    }

    /**
     * Returns user-action descriptors used to build edit controls above the canvas.
     *
     * @return descriptor list; empty when the simulation has no user-edit actions
     */
    protected List<SimulationUserActionDescriptor<CTX>> createUserActionDescriptors() {
        return List.of();
    }

    /**
     * Creates simulation-specific edit option controls shown in expanded edit mode.
     *
     * <p>The returned node is managed by the common edit-toolbar lifecycle and will be made visible/managed only while
     * edit mode is active. Implementations should keep descriptor metadata and JavaFX node creation separate.
     *
     * @param selectedToolId property of the selected stable tool id used for enablement bindings
     * @return option-panel node, or {@code null} when the simulation has no option controls
     */
    @SuppressWarnings("unused")
    protected @Nullable Node createEditToolBarOptionPanel(ObjectProperty<String> selectedToolId) {
        return null;
    }

    /**
     * Registers a cleanup action invoked when the edit toolbar is rebuilt or the simulation shuts down.
     *
     * @param cleanupAction cleanup action for listeners or bindings owned by simulation-specific option controls
     */
    protected final void registerEditToolBarCleanup(Runnable cleanupAction) {
        editToolBar.registerCleanup(cleanupAction);
    }

    protected final Canvas overlayCanvas() {
        return overlayCanvas;
    }

    /**
     * Returns the painters of the canvas layers.
     *
     * @return the current painters, or {@code null} before the simulation is initialized in the view and after the
     * shutdown
     */
    protected final @Nullable CanvasPainters painters() {
        return canvasPainters;
    }

    /**
     * Computes brightness variants of the descriptor color for value groups between {@code min} and {@code max}.
     *
     * @param descriptor     descriptor whose color is varied
     * @param min            lower bound of the values (inclusive)
     * @param max            upper bound of the values (inclusive)
     * @param groupCount     number of brightness groups
     * @param maxFactorDelta total brightness change (positive to brighten, negative to darken)
     * @return the color variants by value, or {@code null} if the descriptor color is not a {@link Color}
     * @see FXPaintFactory#computeBrightnessVariantsMap(Color, int, int, int, double)
     */
    protected final @Nullable Map<Integer, Color> computeBrightnessVariantsMap(GridEntityDescriptor descriptor,
                                                                               int min,
                                                                               int max,
                                                                               int groupCount,
                                                                               double maxFactorDelta) {
        if (!(descriptor.color() instanceof Color baseColor)) {
            return null;
        }
        return FXPaintFactory.computeBrightnessVariantsMap(baseColor, min, max, groupCount, maxFactorDelta);
    }

    protected abstract void handleSelectedGridCellChanged(FXGridCanvasPainter painter,
                                                          @Nullable GC oldGridCell,
                                                          @Nullable GC newGridCell);

    protected abstract void initializeSimulation(CanvasPainters painters, CON config, CellDimension cellDimension);

    /**
     * Draws the current simulation state.
     *
     * @param painters           the painters of the canvas layers
     * @param currentGridModel   the grid model to draw
     * @param stepCount          the step count of the grid model
     * @param lastDrawnStepCount the step count of the previous draw, {@link Integer#MIN_VALUE} before the first draw,
     *                           or {@code stepCount} when the same step is redrawn after a user action; a gap greater
     *                           than {@code 1} means that intermediate steps were not drawn
     */
    @SuppressWarnings("ParameterHidesMemberVariable")
    protected abstract void drawSimulation(CanvasPainters painters, GM currentGridModel, int stepCount,
                                           int lastDrawnStepCount);

    final boolean hasNotificationListener() {
        return notificationListener != null;
    }

    private void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(this::handleSimulationInitialized);
        viewModel.setSimulationResetListener(this::handleSimulationReset);
        viewModel.setSimulationStepListener(this::handleSimulationStep);
        selectedGridCellListener = (_, oldGridCell, newGridCell) -> {
            CanvasPainters currentPainters = painters();
            if (currentPainters != null) {
                handleSelectedGridCellChanged(currentPainters.overlay(), oldGridCell, newGridCell);
            }
        };
        viewModel.selectedGridCellProperty().addListener(selectedGridCellListener);
    }

    private void handleMouseClickedCoordinate(GridCoordinate mouseCoordinate) {
        viewModel.updateClickedCoordinate(mouseCoordinate);
        applySelectedCellUserActionAndRedraw();
    }

    private void rebuildEditToolBar() {
        editToolBar.rebuild(
                createUserActionDescriptors(),
                viewModel.editModeActiveProperty(),
                viewModel.selectedUserActionToolIdProperty(),
                viewModel.simulationStateProperty(),
                this::createEditToolBarOptionPanel,
                this::applyGlobalUserActionAndRedraw);
    }

    private void applyGlobalUserActionAndRedraw(SimulationUserActionDescriptor<CTX> descriptor) {
        boolean changed = viewModel.applyUserAction(descriptor);
        if (changed) {
            redrawAfterUserAction();
        }
    }

    private void applySelectedCellUserActionAndRedraw() {
        boolean changed = viewModel.applySelectedCellUserAction(createUserActionDescriptors());
        if (changed) {
            redrawAfterUserAction();
        }
    }

    private void redrawAfterUserAction() {
        observationView.updateObservationLabels();
        CanvasPainters currentPainters = painters();
        if (currentPainters == null) {
            AppLogger.warnf("%s: Skipping canvas redraw: painters are not initialized", LOG_COMPONENT);
            return;
        }
        drawSimulation(currentPainters, viewModel.getCurrentGridModel(), viewModel.getStepCount(),
                viewModel.getStepCount());
    }

    private void handleSimulationInitialized() {
        int stepCount = viewModel.getStepCount();

        AppLogger.infof("%s: Starting simulation view initialization: step=%d", LOG_COMPONENT, stepCount);

        CanvasPainters newPainters = recreatePaintersAndResizeCanvases(viewModel.getStructure(),
                viewModel.getCellEdgeLength());

        updateCanvasBorderPane(viewModel.getStructure());

        rebuildEditToolBar();

        controlView.setStepCount(stepCount);
        observationView.initializeObservationLabels();

        initializeSimulation(newPainters, viewModel.getCurrentConfig(), newPainters.base().cellDimension());

        AppLogger.infof("%s: Simulation initialized in the view: step=%d", LOG_COMPONENT, stepCount);

        drawAndMeasureSimulationStep(newPainters, stepCount);

        AppLogger.infof("%s: Initial simulation view draw completed: step=%d", LOG_COMPONENT, stepCount);
    }

    private void handleSimulationReset() {
        AppLogger.infof("%s: Resetting simulation view", LOG_COMPONENT);
        clearCanvasLayers();
        controlView.setStepCount(0);
        observationView.initializeObservationLabels();
    }

    private void clearCanvasLayers() {
        CanvasPainters currentPainters = painters();
        if (currentPainters != null) {
            currentPainters.clearCanvasBackgrounds();
        }
        skipOverlayActive = false;
    }

    private void handleSimulationStep(SimulationStepEvent simulationStepEvent) {
        int stepCount = simulationStepEvent.stepCount();
        if (simulationStepEvent.batchModeRunning()) {
            if (DEBUG_MODE) {
                AppLogger.infof("%s: Handling simulation step in view for batch mode: event=%s", LOG_COMPONENT,
                        simulationStepEvent);
            }

            controlView.setStepCount(stepCount);
        } else {
            if (DEBUG_MODE) {
                AppLogger.infof("%s: Handling simulation step in view: event=%s", LOG_COMPONENT, simulationStepEvent);
            }

            controlView.setStepCount(stepCount);

            observationView.updateObservationLabels();

            CanvasPainters currentPainters = painters();
            if (currentPainters == null) {
                AppLogger.warnf("%s: Skipping step draw: painters are not initialized, event=%s", LOG_COMPONENT,
                        simulationStepEvent);
                return;
            }

            // Never draw the same step twice
            if (lastDrawnStepCount != stepCount) {
                throttleAndDrawSimulationStep(currentPainters, stepCount, simulationStepEvent.finalStep(),
                        viewModel.getThrottleDrawMillis());
            } else if (DEBUG_MODE) {
                AppLogger.infof("%s: Skipping draw because step was already drawn: event=%s", LOG_COMPONENT,
                        simulationStepEvent);
            }
        }
    }

    private void drawAndMeasureSimulationStep(CanvasPainters currentPainters, int stepCount) {
        long startNanos = System.nanoTime();
        drawSimulation(currentPainters, viewModel.getCurrentGridModel(), stepCount, lastDrawnStepCount);
        long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        drawThrottler.recordDurationMillis(durationMillis);

        lastDrawnStepCount = stepCount;

        if (DEBUG_MODE) {
            AppLogger.infof("%s: Drawn step %d in %dms: averageDrawMillis=%d",
                    LOG_COMPONENT,
                    stepCount,
                    durationMillis,
                    drawThrottler.getAverageDurationMillis());
        }
    }

    private void throttleAndDrawSimulationStep(CanvasPainters currentPainters,
                                               int stepCount,
                                               boolean finalStep,
                                               long throttleDrawMillis) {
        if (finalStep || !drawThrottler.shouldSkip(throttleDrawMillis)) {
            clearSkipOverlay(currentPainters.overlay());
            drawAndMeasureSimulationStep(currentPainters, stepCount);
        } else {
            showSkipOverlay(currentPainters.overlay());
            if (DEBUG_MODE) {
                AppLogger
                        .infof("%s: Skipping draw for step %d due to high average draw time: averageMillis=%d, "
                                        + "thresholdMillis=%d",
                                LOG_COMPONENT,
                                stepCount,
                                drawThrottler.getAverageDurationMillis(),
                                throttleDrawMillis);
            }
        }
    }

    private void showSkipOverlay(FXGridCanvasPainter overlayPainter) {
        if (!skipOverlayActive) {
            overlayPainter.clearCanvasBackground();

            Point2D center = computeVisibleCanvasCenter();

            overlayPainter.drawCenteredTextWithBackgroundAt(
                    center,
                    SKIP_OVERLAY_SYMBOL,
                    SKIP_OVERLAY_TEXT_COLOR,
                    resolvePreferredFont(SKIP_FONT_SIZE),
                    SKIP_OVERLAY_BACKGROUND_COLOR,
                    SKIP_PADDING
            );
            skipOverlayActive = true;
        }
    }

    private void clearSkipOverlay(FXGridCanvasPainter overlayPainter) {
        if (skipOverlayActive) {
            overlayPainter.clearCanvasBackground();
        }
        skipOverlayActive = false;
    }

    /**
     * Clears the canvases of the current painters and removes the painters; {@link #painters()} returns
     * {@code null} afterwards.
     */
    private void clearPainters() {
        CanvasPainters oldPainters = canvasPainters;
        if (oldPainters != null) {
            oldPainters.clearCanvasBackgrounds();
            canvasPainters = null;
        }
    }

    /**
     * Replaces the base, dynamic, and overlay painters for the given structure and resizes their canvases.
     *
     * <p>The canvas size is limited; if the grid does not fit, the {@link SimulationNotificationType#CANVAS_SIZE_LIMIT}
     * notification is set. Also recreates {@link #cellFont} and {@link #cellEmojiFont}, which are {@code null} when
     * the cells are too small for readable text.
     *
     * @param structure      grid structure to paint
     * @param cellEdgeLength edge length of one cell in pixels
     * @return the new painters
     */
    private CanvasPainters recreatePaintersAndResizeCanvases(GridStructure structure, double cellEdgeLength) {
        clearPainters();

        FXGridCanvasPainter basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
        baseCanvas.setWidth(Math.min(MAX_CANVAS_WIDTH, Math.ceil(basePainter.gridDimension2D().getWidth())));
        baseCanvas.setHeight(Math.min(MAX_CANVAS_HEIGHT, Math.ceil(basePainter.gridDimension2D().getHeight())));

        FXGridCanvasPainter dynamicPainter = new FXGridCanvasPainter(dynamicCanvas, structure, cellEdgeLength);
        dynamicCanvas.setWidth(baseCanvas.getWidth());
        dynamicCanvas.setHeight(baseCanvas.getHeight());

        FXGridCanvasPainter overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
        overlayCanvas.setWidth(baseCanvas.getWidth());
        overlayCanvas.setHeight(baseCanvas.getHeight());

        CanvasPainters newPainters = new CanvasPainters(basePainter, dynamicPainter, overlayPainter);
        canvasPainters = newPainters;
        CellDimension cellDimension = basePainter.cellDimension();

        AppLogger.infof("%s: Canvas painter created: painter=%s", LOG_COMPONENT, basePainter);
        if ((baseCanvas.getWidth() < basePainter.gridDimension2D().getWidth()) ||
                (baseCanvas.getHeight() < basePainter.gridDimension2D().getHeight())) {
            AppLogger.warnf("%s: Canvas size is smaller than the grid dimension", LOG_COMPONENT);
            viewModel.setNotificationType(SimulationNotificationType.CANVAS_SIZE_LIMIT);
        }

        double fontSize = computeCellFontSize(cellDimension, structure.cellShape());
        if (fontSize >= MIN_CELL_FONT_SIZE) {
            cellFont = resolvePreferredFont(fontSize);
            AppLogger.infof("%s: Cell font created: font=%s", LOG_COMPONENT, cellFont);
        } else {
            cellFont = null;
            AppLogger.infof("%s: Cell font not created because font size is too small: fontSize=%.1f", LOG_COMPONENT,
                    fontSize);
        }
        double fontSizeEmoji = computeCellEmojiFontSize(cellDimension, structure.cellShape());
        if (fontSizeEmoji >= MIN_EMOJI_FONT_SIZE) {
            cellEmojiFont = resolvePreferredFont(fontSizeEmoji);
            AppLogger.infof("%s: Cell emoji font created: font=%s", LOG_COMPONENT, cellEmojiFont);
        } else {
            cellEmojiFont = null;
            AppLogger.infof("%s: Cell emoji font not created because font size is too small: fontSize=%.1f",
                    LOG_COMPONENT, fontSizeEmoji);
        }

        return newPainters;
    }

    private void updateCanvasBorderPane(GridStructure structure) {
        canvasBorderPane.setLeft((structure.edgeBehaviorX() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_LEFT_BORDERPANE));
        canvasBorderPane.setRight((structure.edgeBehaviorX() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_RIGHT_BORDERPANE));
        canvasBorderPane.setTop((structure.edgeBehaviorY() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_TOP_BORDERPANE));
        canvasBorderPane.setBottom((structure.edgeBehaviorY() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_BOTTOM_BORDERPANE));
    }

    private Point2D computeVisibleCanvasCenter() {
        double viewportWidth = canvasScrollPane.getViewportBounds().getWidth();
        double viewportHeight = canvasScrollPane.getViewportBounds().getHeight();

        double hValue = canvasScrollPane.getHvalue();
        double vValue = canvasScrollPane.getVvalue();

        double maxX = overlayCanvas.getWidth() - viewportWidth;
        double maxY = overlayCanvas.getHeight() - viewportHeight;
        double x0 = (maxX > 0) ? (hValue * maxX) : 0;
        double y0 = (maxY > 0) ? (vValue * maxY) : 0;

        double x = x0 + (viewportWidth / 2.0d);
        double y = y0 + (viewportHeight / 2.0d);

        return new Point2D(x, y);
    }

    private Font resolvePreferredFont(double size) {
        return fontCache.computeIfAbsent(size, s -> {
            if (Font.getFamilies().contains(MAIN_FONT_FAMILY)) {
                return Font.font(MAIN_FONT_FAMILY, s);
            } else {
                return Font.font(FALLBACK_FONT_FAMILY, s);
            }
        });
    }

    private Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, dynamicCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(dynamicCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);
        stackPane.getStyleClass().add(FXStyleClasses.SIMULATION_STACKPANE);

        canvasBorderPane.setCenter(stackPane);

        canvasScrollPane.setContent(canvasBorderPane);
        canvasScrollPane.setFitToHeight(false);
        canvasScrollPane.setFitToWidth(false);
        canvasScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        canvasScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        canvasScrollPane.setPannable(true);

        VBox vBox = new VBox();
        vBox.getChildren().add(notificationLabel);
        vBox.getChildren().add(editToolBar.getNode());
        vBox.getChildren().add(canvasScrollPane);
        VBox.setVgrow(canvasScrollPane, Priority.ALWAYS);

        return vBox;
    }

    private void registerOverlayPrimaryClickHandler() {
        overlayCanvas.setOnMouseReleased(event -> {
            CanvasPainters currentPainters = painters();
            if ((currentPainters != null)
                    && (event.getButton() == MouseButton.PRIMARY)
                    && event.isStillSincePress()
                    && (event.getClickCount() == 1)) {
                FXGridCanvasPainter painter = currentPainters.overlay();
                Point2D mousePoint = new Point2D(event.getX(), event.getY());
                GridCoordinate mouseCoordinate = GridGeometry.fromCanvasPosition(mousePoint,
                        painter.cellDimension(), painter.gridDimension2D(), painter.gridStructure());
                if (!mouseCoordinate.isIllegal() && painter.isInsideGrid(mouseCoordinate)) {
                    handleMouseClickedCoordinate(mouseCoordinate);
                }
            }
        });
    }

    private void registerNotificationListener() {
        notificationListener = (_, _, newVal) -> {
            if (newVal == SimulationNotificationType.NONE) {
                clearNotification();
            } else {
                updateNotification(AppLocalization.getText(newVal.resourceKey()));
            }
        };
        viewModel.notificationTypeProperty().addListener(notificationListener);
    }

    private void updateNotification(String notification) {
        notificationLabel.setText(notification);
        notificationLabel.setVisible(true);
        notificationLabel.setManaged(true);
    }

    private void clearNotification() {
        notificationLabel.setText(null);
        notificationLabel.setVisible(false);
        notificationLabel.setManaged(false);
    }

    private Region createBorderRegion(String styleClass) {
        Region border = new Region();
        border.getStyleClass().add(styleClass);
        return border;
    }

}
