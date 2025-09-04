package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.model.*;
import de.mkalb.etpetssim.simulations.viewmodel.SimulationMainViewModel;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.jspecify.annotations.Nullable;

import java.util.*;

public abstract class AbstractMainView<
        CON extends SimulationConfig,
        STA extends SimulationStatistics,
        VM extends SimulationMainViewModel,
        CFV extends SimulationConfigView,
        CLV extends SimulationControlView,
        OV extends SimulationObservationView>
        implements SimulationMainView {

    private static final double INITIAL_CANVAS_WIDTH = 640.0d;
    private static final double INITIAL_CANVAS_HEIGHT = 480.0d;
    private static final double MAX_CANVAS_WIDTH = 6_400.0d;
    private static final double MAX_CANVAS_HEIGHT = 4_800.0d;
    private static final String MAIN_FONT_FAMILY = "Verdana";
    private static final String FALLBACK_FONT_FAMILY = "System";
    private static final double MIN_CELL_FONT_SIZE = 7.0d;
    private static final double CENTER_SPLIT_PANE_DIVIDER_POSITION = 0.75d;

    protected final VM viewModel;
    protected final CFV configView;
    protected final CLV controlView;
    protected final OV observationView;
    protected final GridEntityDescriptorRegistry entityDescriptorRegistry;
    protected final Canvas baseCanvas;
    protected final Canvas overlayCanvas;
    private final ScrollPane canvasScrollPane;
    private final BorderPane canvasBorderPane;
    private final Label notificationLabel;
    private final Map<Double, Font> fontCache;

    protected @Nullable FXGridCanvasPainter basePainter;
    protected @Nullable FXGridCanvasPainter overlayPainter;
    protected @Nullable Font cellFont;

    protected AbstractMainView(VM viewModel,
                               CFV configView, CLV controlView, OV observationView,
                               GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.configView = configView;
        this.controlView = controlView;
        this.observationView = observationView;
        this.entityDescriptorRegistry = entityDescriptorRegistry;

        baseCanvas = new Canvas(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT);
        overlayCanvas = new Canvas(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT);
        baseCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
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

    @SuppressWarnings("MagicNumber")
    protected static double computeCellFontSize(CellDimension cellDimension, CellShape cellShape) {
        return Math.round(cellDimension.height() *
                switch (cellShape) {
                    case TRIANGLE -> 0.135d;
                    case SQUARE -> 0.18d;
                    case HEXAGON -> 0.175d;
                });
    }

    @Override
    public final Region buildMainRegion() {
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
        registerOverlayPrimaryMouseEvents();

        return borderPane;
    }

    @Override
    public void shutdownSimulation() {
        viewModel.shutdownSimulation();
    }

    protected abstract void registerViewModelListeners();

    private void registerOverlayPrimaryMouseEvents() {
        BooleanProperty isDragging = new SimpleBooleanProperty(false);

        overlayCanvas.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isDragging.set(false);
            }
        });

        overlayCanvas.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isDragging.set(true);
            }
        });

        overlayCanvas.setOnMouseReleased(event -> {
            if ((overlayPainter != null)
                    && (event.getButton() == MouseButton.PRIMARY)
                    && !isDragging.get()
                    && (event.getClickCount() == 1)) {
                Point2D mousePoint = new Point2D(event.getX(), event.getY());
                GridCoordinate mouseCoordinate = GridGeometry.fromCanvasPosition(mousePoint,
                        overlayPainter.cellDimension(), overlayPainter.gridDimension2D(), overlayPainter.gridStructure());
                if (!mouseCoordinate.isIllegal() && !overlayPainter.isOutsideGrid(mouseCoordinate)) {
                    handleMouseClickedCoordinate(mousePoint, mouseCoordinate, overlayPainter);
                }
            }
        });
    }

    protected abstract void handleMouseClickedCoordinate(Point2D mousePoint, GridCoordinate mouseCoordinate, FXGridCanvasPainter painter);

    private void registerNotificationListener() {
        viewModel.notificationTypeProperty().addListener((_, _, newVal) -> {
            if (newVal == SimulationNotificationType.NONE) {
                clearNotification();
            } else {
                updateNotification(AppLocalization.getText(newVal.resourceKey()));
            }
        });
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

    protected abstract List<Node> createModificationToolbarNodes();

    protected final Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
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

        var modificationNodes = createModificationToolbarNodes();
        if (!modificationNodes.isEmpty()) {
            ToolBar modificationToolbar = new ToolBar();
            modificationToolbar.getItems().addAll(modificationNodes);
            modificationToolbar.getStyleClass().add(FXStyleClasses.SIMULATION_TOOLBAR);
            modificationToolbar.visibleProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.PAUSED));
            modificationToolbar.managedProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.PAUSED));
            vBox.getChildren().add(modificationToolbar);
        }

        vBox.getChildren().add(canvasScrollPane);
        VBox.setVgrow(canvasScrollPane, Priority.ALWAYS);

        return vBox;
    }

    protected final void createPainterAndUpdateCanvas(GridStructure structure, double cellEdgeLength) {
        basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
        baseCanvas.setWidth(Math.min(MAX_CANVAS_WIDTH, basePainter.gridDimension2D().getWidth()));
        baseCanvas.setHeight(Math.min(MAX_CANVAS_HEIGHT, basePainter.gridDimension2D().getHeight()));

        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
        overlayCanvas.setWidth(Math.min(MAX_CANVAS_WIDTH, overlayPainter.gridDimension2D().getWidth()));
        overlayCanvas.setHeight(Math.min(MAX_CANVAS_HEIGHT, overlayPainter.gridDimension2D().getHeight()));

        AppLogger.info("MainView: Canvas painter created: " + basePainter);
        if ((baseCanvas.getWidth() < basePainter.gridDimension2D().getWidth()) ||
                (baseCanvas.getHeight() < basePainter.gridDimension2D().getHeight())) {
            AppLogger.warn("MainView: Canvas size is smaller than the grid dimension.");
            viewModel.setNotificationType(SimulationNotificationType.CANVAS_SIZE_LIMIT);
        }

        // Font
        double fontSize = computeCellFontSize(basePainter.cellDimension(), structure.cellShape());
        if (fontSize >= MIN_CELL_FONT_SIZE) {
            cellFont = getPreferredFont(fontSize);
            AppLogger.info("MainView: Cell font created: " + cellFont);
        } else {
            cellFont = null;
            AppLogger.info("MainView: Cell font not created, because font size is too small: " + fontSize);
        }
    }

    protected final void updateCanvasBorderPane(GridStructure structure) {
        canvasBorderPane.setLeft((structure.edgeBehaviorX() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_LEFT_BORDERPANE));
        canvasBorderPane.setRight((structure.edgeBehaviorX() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_RIGHT_BORDERPANE));
        canvasBorderPane.setTop((structure.edgeBehaviorY() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_TOP_BORDERPANE));
        canvasBorderPane.setBottom((structure.edgeBehaviorY() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_BOTTOM_BORDERPANE));
    }

    private Region createBorderRegion(String styleClass) {
        Region border = new Region();
        border.getStyleClass().add(styleClass);
        return border;
    }

    @SuppressWarnings("MagicNumber")
    protected final Point2D computeVisibleCanvasCenter(Canvas canvas) {
        double viewportWidth = canvasScrollPane.getViewportBounds().getWidth();
        double viewportHeight = canvasScrollPane.getViewportBounds().getHeight();

        double hValue = canvasScrollPane.getHvalue();
        double vValue = canvasScrollPane.getVvalue();

        double maxX = canvas.getWidth() - viewportWidth;
        double maxY = canvas.getHeight() - viewportHeight;
        double x0 = (maxX > 0) ? (hValue * maxX) : 0;
        double y0 = (maxY > 0) ? (vValue * maxY) : 0;

        double x = x0 + (viewportWidth / 2.0d);
        double y = y0 + (viewportHeight / 2.0d);

        return new Point2D(x, y);
    }

    protected final Font getPreferredFont(double size) {
        return fontCache.computeIfAbsent(size, s -> {
            if (Font.getFamilies().contains(MAIN_FONT_FAMILY)) {
                return Font.font(MAIN_FONT_FAMILY, s);
            } else {
                return Font.font(FALLBACK_FONT_FAMILY, s);
            }
        });
    }

}
