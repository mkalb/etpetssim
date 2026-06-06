package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationMainViewModel;
import de.mkalb.etpetssim.ui.*;
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

/**
 * Generic base view that composes config, control and observation regions.
 */
public abstract class AbstractMainView<
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
    private static final double MIN_EMOJI_FONT_SIZE = 3.0d;
    private static final double CENTER_SPLIT_PANE_DIVIDER_POSITION = 0.75d;
    private static final String LOG_COMPONENT = "MainView";

    protected final VM viewModel;
    protected final CFV configView;
    protected final CLV controlView;
    protected final OV observationView;
    protected final GridEntityDescriptorRegistry entityDescriptorRegistry;
    protected final Canvas baseCanvas;
    protected final Canvas dynamicCanvas;
    protected final Canvas overlayCanvas;
    private final ScrollPane canvasScrollPane;
    private final BorderPane canvasBorderPane;
    private final Label notificationLabel;
    private final ToolBar actionToolBar;
    private final Map<Double, Font> fontCache;

    protected @Nullable FXGridCanvasPainter basePainter;
    protected @Nullable FXGridCanvasPainter dynamicPainter;
    protected @Nullable FXGridCanvasPainter overlayPainter;
    protected @Nullable Font cellFont;
    protected @Nullable Font cellEmojiFont;

    protected AbstractMainView(VM viewModel,
                               CFV configView, CLV controlView, OV observationView,
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

        actionToolBar = new ToolBar();
        actionToolBar.getStyleClass().add(FXStyleClasses.SIMULATION_TOOLBAR);
        clearActionToolBar();
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

    @SuppressWarnings("MagicNumber")
    protected static double computeCellEmojiFontSize(CellDimension cellDimension, CellShape cellShape) {
        return Math.round(cellDimension.height() *
                switch (cellShape) {
                    case TRIANGLE -> 0.6d;
                    case SQUARE -> 0.9d;
                    case HEXAGON -> 0.8d;
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
        registerOverlayPrimaryClickHandler();

        return borderPane;
    }

    @Override
    public void shutdownSimulation() {
        viewModel.shutdownSimulation();
        basePainter = null;
        dynamicPainter = null;
        overlayPainter = null;
        cellFont = null;
        cellEmojiFont = null;
        clearNotification();
        clearActionToolBar();
    }

    protected abstract void registerViewModelListeners();

    private void registerOverlayPrimaryClickHandler() {
        overlayCanvas.setOnMouseReleased(event -> {
            FXGridCanvasPainter painter = overlayPainter;
            if ((painter != null)
                    && (event.getButton() == MouseButton.PRIMARY)
                    && event.isStillSincePress()
                    && (event.getClickCount() == 1)) {
                Point2D mousePoint = new Point2D(event.getX(), event.getY());
                GridCoordinate mouseCoordinate = GridGeometry.fromCanvasPosition(mousePoint,
                        painter.cellDimension(), painter.gridDimension2D(), painter.gridStructure());
                if (!mouseCoordinate.isIllegal() && painter.isInsideGrid(mouseCoordinate)) {
                    handleMouseClickedCoordinate(mousePoint, mouseCoordinate, painter);
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

    private void clearActionToolBar() {
        actionToolBar.getItems().clear();
        actionToolBar.disableProperty().unbind();
        actionToolBar.setDisable(true);
        actionToolBar.setVisible(false);
        actionToolBar.setManaged(false);
    }

    protected final void rebuildActionToolBar() {
        clearActionToolBar();
        List<Node> nodes = createActionToolBarNodes();
        if (!nodes.isEmpty()) {
            actionToolBar.getItems().setAll(nodes);
            actionToolBar.disableProperty().bind(viewModel.simulationStateProperty().isNotEqualTo(SimulationState.PAUSED));
            actionToolBar.setVisible(true);
            actionToolBar.setManaged(true);
        }
    }

    protected abstract List<Node> createActionToolBarNodes();

    protected final Region createSimulationRegion() {
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
        vBox.getChildren().add(actionToolBar);
        vBox.getChildren().add(canvasScrollPane);
        VBox.setVgrow(canvasScrollPane, Priority.ALWAYS);

        return vBox;
    }

    protected final CellDimension createPainterAndUpdateCanvas(GridStructure structure, double cellEdgeLength) {
        if ((basePainter != null) && (dynamicPainter != null) && (overlayPainter != null)) {
            basePainter.clearCanvasBackground();
            dynamicPainter.clearCanvasBackground();
            overlayPainter.clearCanvasBackground();
            basePainter = null;
            dynamicPainter = null;
            overlayPainter = null;
        }

        basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
        baseCanvas.setWidth(Math.min(MAX_CANVAS_WIDTH, Math.ceil(basePainter.gridDimension2D().getWidth())));
        baseCanvas.setHeight(Math.min(MAX_CANVAS_HEIGHT, Math.ceil(basePainter.gridDimension2D().getHeight())));

        dynamicPainter = new FXGridCanvasPainter(dynamicCanvas, structure, cellEdgeLength);
        dynamicCanvas.setWidth(baseCanvas.getWidth());
        dynamicCanvas.setHeight(baseCanvas.getHeight());

        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
        overlayCanvas.setWidth(baseCanvas.getWidth());
        overlayCanvas.setHeight(baseCanvas.getHeight());

        CellDimension cellDimension = basePainter.cellDimension();

        AppLogger.infof("%s: Canvas painter created. painter=%s", LOG_COMPONENT, basePainter);
        if ((baseCanvas.getWidth() < basePainter.gridDimension2D().getWidth()) ||
                (baseCanvas.getHeight() < basePainter.gridDimension2D().getHeight())) {
            AppLogger.warnf("%s: Canvas size is smaller than the grid dimension.", LOG_COMPONENT);
            viewModel.setNotificationType(SimulationNotificationType.CANVAS_SIZE_LIMIT);
        }

        // Font
        double fontSize = computeCellFontSize(cellDimension, structure.cellShape());
        if (fontSize >= MIN_CELL_FONT_SIZE) {
            cellFont = getPreferredFont(fontSize);
            AppLogger.infof("%s: Cell font created. font=%s", LOG_COMPONENT, cellFont);
        } else {
            cellFont = null;
            AppLogger.infof("%s: Cell font not created because font size is too small. fontSize=%.1f", LOG_COMPONENT, fontSize);
        }
        double fontSizeEmoji = computeCellEmojiFontSize(cellDimension, structure.cellShape());
        if (fontSizeEmoji >= MIN_EMOJI_FONT_SIZE) {
            cellEmojiFont = getPreferredFont(fontSizeEmoji);
            AppLogger.infof("%s: Cell emoji font created. font=%s", LOG_COMPONENT, cellEmojiFont);
        } else {
            cellEmojiFont = null;
            AppLogger.infof("%s: Cell emoji font not created because font size is too small. fontSize=%.1f", LOG_COMPONENT, fontSizeEmoji);
        }

        return cellDimension;
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
