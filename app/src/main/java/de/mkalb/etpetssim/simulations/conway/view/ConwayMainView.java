package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.EdgeBehavior;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.model.GridEntityUtils;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.SimulationView;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayMainViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractMainView;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

import java.util.*;

@SuppressWarnings("MagicNumber")
public final class ConwayMainView extends AbstractMainView<ConwayMainViewModel> implements SimulationView {

    private static final double INITIAL_CANVAS_SIZE = 100.0d;

    private final ConwayConfigView configView;
    private final ConwayControlView controlView;
    private final ConwayObservationView observationView;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final Canvas baseCanvas;
    private final Canvas overlayCanvas;
    private final BorderPane canvasBorderPane;

    private @Nullable FXGridCanvasPainter basePainter;
    private @Nullable FXGridCanvasPainter overlayPainter;

    public ConwayMainView(ConwayMainViewModel viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          ConwayConfigView configView,
                          ConwayControlView controlView,
                          ConwayObservationView observationView) {
        super(viewModel);
        this.configView = configView;
        this.observationView = observationView;
        this.controlView = controlView;
        this.entityDescriptorRegistry = entityDescriptorRegistry;

        baseCanvas = new Canvas(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE);
        overlayCanvas = new Canvas(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE);
        baseCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        overlayCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        canvasBorderPane = new BorderPane();
    }

    @Override
    public Region buildRegion() {
        Region configRegion = configView.buildRegion();
        Region simulationRegion = createSimulationRegion();
        Region controlRegion = controlView.buildRegion();
        Region observationRegion = observationView.buildRegion();

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(configRegion);
        borderPane.setCenter(simulationRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setRight(observationRegion);
        borderPane.getStyleClass().add(FXStyleClasses.VIEW_BORDERPANE);

        registerViewModelListeners();

        return borderPane;
    }

    private void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(() -> {
            double cellEdgeLength = viewModel.getCellEdgeLength();
            ReadableGridModel<ConwayEntity> currentModel = Objects.requireNonNull(viewModel.getCurrentModel());
            GridStructure structure = viewModel.getStructure();
            long currentStep = viewModel.getCurrentStep();

            AppLogger.info("Initialize canvas and painter with structure " + structure.toDisplayString() +
                    " and cell edge length " + cellEdgeLength);

            basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
            baseCanvas.setWidth(Math.min(6_000.0d, basePainter.gridDimension2D().getWidth()));
            baseCanvas.setHeight(Math.min(4_000.0d, basePainter.gridDimension2D().getHeight()));

            overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
            overlayCanvas.setWidth(Math.min(5_000.0d, overlayPainter.gridDimension2D().getWidth()));
            overlayCanvas.setHeight(Math.min(3_000.0d, overlayPainter.gridDimension2D().getHeight()));

            updateCanvasBorderPane(structure);

            drawCanvas(currentModel, currentStep);
            observationView.updateObservationLabels();
        });
        viewModel.setSimulationStepListener(() -> {
            ReadableGridModel<ConwayEntity> currentModel = Objects.requireNonNull(viewModel.getCurrentModel());
            long currentStep = viewModel.getCurrentStep();
            AppLogger.info("Drawing canvas for step " + currentStep);

            drawCanvas(currentModel, currentStep);
            observationView.updateObservationLabels();
        });
    }

    private void drawCanvas(ReadableGridModel<ConwayEntity> currentModel, long currentStep) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        // Fill background
        basePainter.fillCanvasBackground(javafx.scene.paint.Color.BLACK);
        basePainter.fillGridBackground(javafx.scene.paint.Color.WHITE);

        // Draw all cells
        currentModel.structure()
                    .coordinatesStream()
                    .forEachOrdered(coordinate ->
                            GridEntityUtils.consumeDescriptorAt(
                                    coordinate,
                                    currentModel,
                                    entityDescriptorRegistry,
                                    descriptor ->
                                            basePainter.drawCell(
                                                    coordinate,
                                                    descriptor.colorAsOptional().orElse(Color.BLACK),
                                                    null,
                                                    0.0d)));
    }

    private Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);
        stackPane.getStyleClass().add(FXStyleClasses.SIMULATION_STACKPANE);

        canvasBorderPane.setCenter(stackPane);
        canvasBorderPane.getStyleClass().add(FXStyleClasses.SIMULATION_CENTER_BORDERPANE);

        ScrollPane scrollPane = new ScrollPane(canvasBorderPane);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add(FXStyleClasses.SIMULATION_SCROLLPANE);

        return scrollPane;
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

    private Region createBorderRegion(String styleClass) {
        Region border = new Region();
        border.getStyleClass().add(styleClass);
        return border;
    }

}

