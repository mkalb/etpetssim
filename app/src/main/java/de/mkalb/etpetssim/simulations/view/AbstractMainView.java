package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.EdgeBehavior;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.viewmodel.SimulationMainViewModel;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.jspecify.annotations.Nullable;

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

    protected final VM viewModel;
    protected final CFV configView;
    protected final CLV controlView;
    protected final OV observationView;
    protected final GridEntityDescriptorRegistry entityDescriptorRegistry;
    protected final Canvas baseCanvas;
    protected final Canvas overlayCanvas;
    private final BorderPane canvasBorderPane;
    private final Label notificationLabel;

    protected @Nullable FXGridCanvasPainter basePainter;
    protected @Nullable FXGridCanvasPainter overlayPainter;

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

        notificationLabel = new Label();
        notificationLabel.getStyleClass().add(FXStyleClasses.VIEW_NOTIFICATION_LABEL);
        clearNotification();
    }

    @Override
    public final Region buildMainRegion() {
        Region configRegion = configView.buildConfigRegion();
        Region controlRegion = controlView.buildControlRegion();
        Region observationRegion = observationView.buildObservationRegion();
        Region simulationRegion = createSimulationRegion();

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FXStyleClasses.VIEW_BORDERPANE);

        borderPane.setTop(configRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setRight(observationRegion);
        borderPane.setCenter(simulationRegion);

        registerViewModelListeners();
        registerTimeoutListener();

        return borderPane;
    }

    @Override
    public void shutdownSimulation() {
        viewModel.shutdownSimulation();
    }

    protected abstract void registerViewModelListeners();

    private void registerTimeoutListener() {
        viewModel.simulationTimeoutProperty().addListener((_, _, newVal) -> {
            if (Boolean.TRUE.equals(newVal)) {
                updateNotification(AppLocalization.getText(AppLocalizationKeys.NOTIFICATION_SIMULATION_TIMEOUT));
            } else {
                clearNotification();
            }
        });
    }

    protected final void updateNotification(String notification) {
        notificationLabel.setText(notification);
        notificationLabel.setVisible(true);
        notificationLabel.setManaged(true);
    }

    protected final void clearNotification() {
        notificationLabel.setText(null);
        notificationLabel.setVisible(false);
        notificationLabel.setManaged(false);
    }

    protected final Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);
        stackPane.getStyleClass().add(FXStyleClasses.SIMULATION_STACKPANE);

        canvasBorderPane.setCenter(stackPane);

        ScrollPane scrollPane = new ScrollPane(canvasBorderPane);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add(FXStyleClasses.SIMULATION_SCROLLPANE);

        return new VBox(notificationLabel, scrollPane);
    }

    protected final void createPainterAndUpdateCanvas(GridStructure structure, double cellEdgeLength) {
        basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
        baseCanvas.setWidth(Math.min(MAX_CANVAS_WIDTH, basePainter.gridDimension2D().getWidth()));
        baseCanvas.setHeight(Math.min(MAX_CANVAS_HEIGHT, basePainter.gridDimension2D().getHeight()));

        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
        overlayCanvas.setWidth(Math.min(MAX_CANVAS_WIDTH, overlayPainter.gridDimension2D().getWidth()));
        overlayCanvas.setHeight(Math.min(MAX_CANVAS_HEIGHT, overlayPainter.gridDimension2D().getHeight()));

        AppLogger.info("Canvas painter created: " + basePainter);
        if ((baseCanvas.getWidth() < basePainter.gridDimension2D().getWidth()) ||
                (baseCanvas.getHeight() < basePainter.gridDimension2D().getHeight())) {
            AppLogger.warn("Canvas size is smaller than the grid dimension.");
            updateNotification(AppLocalization.getText(AppLocalizationKeys.NOTIFICATION_CANVAS_SIZE_LIMIT));
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

}
