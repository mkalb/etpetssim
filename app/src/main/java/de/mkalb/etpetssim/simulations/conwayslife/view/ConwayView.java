package de.mkalb.etpetssim.simulations.conwayslife.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.model.GridEntityUtils;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayViewModel;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

import java.util.*;

@SuppressWarnings("MagicNumber")
public class ConwayView extends StackPane {

    private final ConwayViewModel viewModel;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final Canvas baseCanvas;
    private final Canvas overlayCanvas;
    private final ConwayObservationView observationView;
    private @Nullable FXGridCanvasPainter painter;
    private @Nullable FXGridCanvasPainter overlayPainter;

    public ConwayView(ConwayViewModel viewModel, GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.entityDescriptorRegistry = entityDescriptorRegistry;

        baseCanvas = new Canvas(100, 100);
        overlayCanvas = new Canvas(100, 100);

        observationView = new ConwayObservationView(viewModel);
    }

    public Region buildViewRegion() {
        Region configRegion = createConfigRegion();
        Region simulationRegion = createSimulationRegion();
        Region controlRegion = createControlRegion();
        Region observationRegion = observationView.buildObservationRegion();

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(configRegion);
        borderPane.setCenter(simulationRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setRight(observationRegion);

        borderPane.getStyleClass().add("simulation-border-pane");

        registerViewModelListeners();

        return borderPane;
    }

    private void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(() -> {
            double cellEdgeLength = viewModel.getCellEdgeLength();
            ReadableGridModel<ConwayEntity> currentModel = Objects.requireNonNull(viewModel.getCurrentModel());
            GridStructure structure = viewModel.getGridStructure();
            long currentStep = viewModel.getCurrentStep();

            AppLogger.info("Initialize canvas and painter with structure " + structure.toDisplayString() +
                    " and cell edge length " + cellEdgeLength);

            painter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
            baseCanvas.setWidth(Math.min(6_000.0d, painter.gridDimension2D().getWidth()));
            baseCanvas.setHeight(Math.min(4_000.0d, painter.gridDimension2D().getHeight()));

            overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
            overlayCanvas.setWidth(Math.min(5_000.0d, overlayPainter.gridDimension2D().getWidth()));
            overlayCanvas.setHeight(Math.min(3_000.0d, overlayPainter.gridDimension2D().getHeight()));

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
        if (painter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        // Fill background
        painter.fillCanvasBackground(javafx.scene.paint.Color.BLACK);
        painter.fillGridBackground(javafx.scene.paint.Color.WHITE);

        // Draw all cells
        currentModel.structure().coordinatesStream().forEachOrdered(coordinate -> {
            GridEntityUtils.consumeDescriptorAt(coordinate, currentModel, entityDescriptorRegistry,
                    descriptor -> painter.drawCell(coordinate, descriptor.colorAsOptional().orElse(Color.BLACK),
                            Color.BLACK, 1.0d));
        });
    }

    private Region createConfigRegion() {
        Label widthLabel = new Label("Grid Width:");
        Spinner<Integer> widthSpinner = new Spinner<>(8, 16_384, viewModel.getGridWidth(), 2);
        widthSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.gridWidthProperty().asObject());

        Label heightLabel = new Label("Grid Height:");
        Spinner<Integer> heightSpinner = new Spinner<>(8, 16_384, viewModel.getGridHeight(), 2);
        heightSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.gridHeightProperty().asObject());

        Label cellLabel = new Label("Cell Edge Length:");
        Slider cellSlider = new Slider(5, 50, viewModel.getCellEdgeLength());
        cellSlider.setShowTickLabels(true);
        cellSlider.setShowTickMarks(true);
        cellSlider.valueProperty().bindBidirectional(viewModel.cellEdgeLengthProperty());

        Label percentLabel = new Label("Alive %:");
        Slider percentSlider = new Slider(0.0, 1.0, viewModel.getAlivePercent());
        percentSlider.setShowTickLabels(true);
        percentSlider.setShowTickMarks(true);
        percentSlider.setMajorTickUnit(0.1);
        percentSlider.setMinorTickCount(4);
        percentSlider.setBlockIncrement(0.01);
        percentSlider.valueProperty().bindBidirectional(viewModel.alivePercentProperty());

        HBox hbox = new HBox(10, widthLabel, widthSpinner, heightLabel, heightSpinner, cellLabel, cellSlider, percentLabel, percentSlider);
        hbox.setAlignment(Pos.CENTER_LEFT);

        // Bind disableProperty directly to simulationStateProperty
        hbox.disableProperty().bind(
                viewModel.simulationStateProperty().isNotEqualTo(SimulationState.READY)
        );

        return hbox;
    }

    private Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(stackPane);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("simulation-scroll-pane");

        return scrollPane;
    }

    private Region createControlRegion() {
        Button actionButton = buildControlButton("Action", false);
        Button cancelButton = buildControlButton("Cancel", true);

        actionButton.textProperty().bind(
                Bindings.createStringBinding(() -> switch (viewModel.getSimulationState()) {
                    case READY -> "Start";
                    case RUNNING -> "Pause";
                    case PAUSED -> "Resume";
                }, viewModel.simulationStateProperty())
        );
        cancelButton.disableProperty().bind(
                viewModel.simulationStateProperty().isEqualTo(SimulationState.READY)
        );

        actionButton.setOnAction(e -> viewModel.onActionButton());
        cancelButton.setOnAction(e -> viewModel.onCancelButton());

        HBox hbox = new HBox();
        hbox.getChildren().addAll(actionButton, cancelButton);
        hbox.getStyleClass().add("control-hbox");

        return hbox;
    }

    private Button buildControlButton(String text, boolean disabled) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add("control-button");
        controlButton.setDisable(disabled);
        return controlButton;
    }

}

