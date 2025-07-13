package de.mkalb.etpetssim.simulations.conwayslife.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayViewModel;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("MagicNumber")
public class ConwayView extends StackPane {

    private final ConwayViewModel viewModel;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final Canvas baseCanvas;
    private final Canvas overlayCanvas;
    private @Nullable FXGridCanvasPainter painter;
    private @Nullable FXGridCanvasPainter overlayPainter;

    public ConwayView(ConwayViewModel viewModel, GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.entityDescriptorRegistry = entityDescriptorRegistry;

        baseCanvas = new Canvas(100, 100);
        overlayCanvas = new Canvas(100, 100);
    }

    public Region buildViewRegion() {
        // Register callback
        viewModel.setModelCreatedListener((structure) -> {
            AppLogger.info("Structure:       " + structure.toDisplayString());
            double cellEdgeLength = viewModel.getCellEdgeLength();
            painter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
            baseCanvas.setWidth(Math.min(6_000.0d, painter.gridDimension2D().getWidth()));
            baseCanvas.setHeight(Math.min(4_000.0d, painter.gridDimension2D().getHeight()));

            overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
            overlayCanvas.setWidth(Math.min(5_000.0d, overlayPainter.gridDimension2D().getWidth()));
            overlayCanvas.setHeight(Math.min(3_000.0d, overlayPainter.gridDimension2D().getHeight()));
        });

        Region configRegion = createConfigRegion();
        Region simulationRegion = createSimulationRegion();
        Region controlRegion = createControlRegion();
        Region observationRegion = createObservationRegion();
        observationRegion.setVisible(false);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(configRegion);
        borderPane.setCenter(simulationRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setRight(observationRegion);

        borderPane.getStyleClass().add("simulation-border-pane");

        return borderPane;
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

        HBox hbox = new HBox(10, widthLabel, widthSpinner, heightLabel, heightSpinner, cellLabel, cellSlider);
        hbox.setAlignment(Pos.CENTER_LEFT);
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
        Button startButton = buildControlButton("Start", false);
        Button resumeButton = buildControlButton("Resume", true);
        Button pauseButton = buildControlButton("Pause", true);
        Button cancelButton = buildControlButton("Cancel", true);

        startButton.setOnAction(e -> viewModel.startSimulation());

        HBox hbox = new HBox();
        hbox.getChildren().addAll(startButton, cancelButton);
        hbox.getStyleClass().add("control-hbox");

        return hbox;
    }

    private Button buildControlButton(String text, boolean disabled) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add("control-button");
        controlButton.setDisable(disabled);
        return controlButton;
    }

    private Region createObservationRegion() {
        return new HBox(); // TODO Implement later
    }

}

