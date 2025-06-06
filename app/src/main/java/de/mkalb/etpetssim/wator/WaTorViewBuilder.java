package de.mkalb.etpetssim.wator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import org.jspecify.annotations.Nullable;

import java.net.URL;
import java.util.*;
import java.util.function.*;

public final class WaTorViewBuilder implements Builder<Region> {

    private final WaTorModel waTorModel;
    private final BooleanSupplier startSimulationSupplier;
    private final BooleanSupplier updateSimulationSupplier;
    private @Nullable Timeline timeline;

    public WaTorViewBuilder(WaTorModel waTorModel, BooleanSupplier startSimulationSupplier, BooleanSupplier updateSimulationSupplier) {
        this.waTorModel = waTorModel;
        this.startSimulationSupplier = startSimulationSupplier;
        this.updateSimulationSupplier = updateSimulationSupplier;
    }

    @Override
    public Region build() {
        WaTorCanvasRenderer canvasRenderer = new WaTorCanvasRenderer(waTorModel);

        Region configRegion = createConfigRegion();
        Region simulationRegion = createSimulationRegion(canvasRenderer.simulationCanvas());
        Region controlRegion = createControlRegion(
                canvasRenderer,
                () -> configRegion.setDisable(false),
                () -> configRegion.setDisable(true)
        );
        Region observationRegion = createObservationRegion();

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(configRegion);
        borderPane.setCenter(simulationRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setRight(observationRegion);

        ScrollPane scrollPane = new ScrollPane(borderPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        // load and add CSS styles
        URL cssURL = Objects.requireNonNull(getClass().getResource("wator-styles.css"), "CSS not found: wator-styles.css");
        scrollPane.getStylesheets().add(cssURL.toExternalForm());

        return scrollPane;
    }

    private Region createConfigRegion() {
        ChoiceBox<Integer> choiceBox = new ChoiceBox<>(waTorModel.cellLengthChoices());
        choiceBox.valueProperty().bindBidirectional(waTorModel.cellLengthProperty().asObject());

        Slider xSizeSlider = new Slider(WaTorModel.MIN_SIZE, Math.round(2048 / waTorModel.cellLength()), waTorModel.xSize());
        initConfigSlider(xSizeSlider, 128 / waTorModel.cellLength(), waTorModel.xSizeProperty());

        Slider ySizeSlider = new Slider(WaTorModel.MIN_SIZE, Math.round(2048 / waTorModel.cellLength()), waTorModel.ySize());
        initConfigSlider(ySizeSlider, 128 / waTorModel.cellLength(), waTorModel.ySizeProperty());

        Slider fishNumberSlider = new Slider(0.0d, Math.round(waTorModel.xSize() * waTorModel.ySize()) / 4, waTorModel.fishNumber());
        initConfigSlider(fishNumberSlider, Math.round(waTorModel.xSize() * waTorModel.ySize()) / 32, waTorModel.fishNumberProperty());

        Slider sharkNumberSlider = new Slider(0.0d, Math.round(waTorModel.xSize() * waTorModel.ySize()) / 8, waTorModel.sharkNumber());
        initConfigSlider(sharkNumberSlider, Math.round(waTorModel.xSize() * waTorModel.ySize()) / 64, waTorModel.sharkNumberProperty());

        // Listener
        choiceBox.valueProperty().addListener((obs, oldval, newVal) -> {
            xSizeSlider.setMax(Math.round(2048 / newVal.intValue()));
            ySizeSlider.setMax(Math.round(2048 / newVal.intValue()));
            xSizeSlider.setMajorTickUnit(128 / newVal.intValue());
            ySizeSlider.setMajorTickUnit(128 / newVal.intValue());
        });
        xSizeSlider.valueProperty().addListener((obs, oldval, newVal) -> {
            xSizeSlider.setValue(newVal.intValue());
            fishNumberSlider.setMax(Math.round(newVal.intValue() * waTorModel.ySize()) / 4);
            sharkNumberSlider.setMax(Math.round(newVal.intValue() * waTorModel.ySize()) / 8);
            fishNumberSlider.setMajorTickUnit(Math.round(newVal.intValue() * waTorModel.ySize()) / 32);
            sharkNumberSlider.setMajorTickUnit(Math.round(newVal.intValue() * waTorModel.ySize()) / 64);
        });
        ySizeSlider.valueProperty().addListener((obs, oldval, newVal) -> {
            ySizeSlider.setValue(newVal.intValue());
            fishNumberSlider.setMax(Math.round(waTorModel.xSize() * newVal.intValue()) / 4);
            sharkNumberSlider.setMax(Math.round(waTorModel.xSize() * newVal.intValue()) / 8);
            fishNumberSlider.setMajorTickUnit(Math.round(waTorModel.xSize() * newVal.intValue()) / 32);
            sharkNumberSlider.setMajorTickUnit(Math.round(waTorModel.xSize() * newVal.intValue()) / 64);
        });
        fishNumberSlider.valueProperty().addListener((obs, oldval, newVal) ->
                fishNumberSlider.setValue(newVal.intValue()));
        sharkNumberSlider.valueProperty().addListener((obs, oldval, newVal) ->
                sharkNumberSlider.setValue(newVal.intValue()));

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("config-grid-pane");

        gridPane.add(new Label("Cell length"), 0, 0);
        gridPane.add(new Label("X size"), 0, 1);
        gridPane.add(new Label("Y size"), 0, 2);
        gridPane.add(new Label("Fish number"), 0, 3);
        gridPane.add(new Label("Shark number"), 0, 4);

        gridPane.add(choiceBox, 1, 0);
        gridPane.add(xSizeSlider, 1, 1);
        gridPane.add(ySizeSlider, 1, 2);
        gridPane.add(fishNumberSlider, 1, 3);
        gridPane.add(sharkNumberSlider, 1, 4);

        return gridPane;
    }

    private void initConfigSlider(Slider slider, int majorTickUnit, IntegerProperty integerProperty) {
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(majorTickUnit);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1.0d);
        slider.valueProperty().bindBidirectional(integerProperty);
        slider.getStyleClass().add("config-slider");
    }

    private Region createSimulationRegion(Canvas simulationCanvas) {
        ScrollPane scrollPane = new ScrollPane(simulationCanvas);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("simulation-scroll-pane");
        return scrollPane;
    }

    private Region createControlRegion(WaTorCanvasRenderer canvasRenderer,
                                       Runnable enableConfigRegionRunnable, Runnable disableConfigRegionRunnable) {
        Button startButton = buildControlButton("Start");
        Button resumeButton = buildControlButton("Resume");
        Button pauseButton = buildControlButton("Pause");
        Button cancelButton = buildControlButton("Cancel");

        Slider speedSlider = new Slider(1.0d, 25.0d, waTorModel.speedAsTenthOfASecond());
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(3.0d);
        speedSlider.setMinorTickCount(0);
        speedSlider.setBlockIncrement(1.0d);
        speedSlider.valueProperty().bindBidirectional(waTorModel.speedProperty());
        speedSlider.valueProperty().addListener((obs, oldval, newVal) ->
                speedSlider.setValue(newVal.intValue()));
        speedSlider.getStyleClass().add("control-slider");

        HBox hbox = new HBox();
        hbox.getChildren().addAll(startButton, cancelButton, speedSlider);
        hbox.getStyleClass().add("control-hbox");

        Runnable actionAfterSimulationStopped = () -> {
            startButton.setDisable(true);
            resumeButton.setDisable(true);
            pauseButton.setDisable(true);
            cancelButton.setDisable(true);
            speedSlider.setDisable(true);
            enableConfigRegionRunnable.run();
            hbox.getChildren().set(0, startButton); // Replace pauseButton with startButton
            startButton.setDisable(false);
            speedSlider.setDisable(false);
        };
        startButton.setOnAction(event -> {
            stopTimeline();

            startButton.setDisable(true);
            resumeButton.setDisable(true);
            pauseButton.setDisable(true);
            cancelButton.setDisable(true);
            speedSlider.setDisable(true);

            canvasRenderer.prepareStart();

            boolean finished = startSimulationSupplier.getAsBoolean();
            if (finished) {
                startButton.setDisable(false);
                speedSlider.setDisable(false);
                // TODO Show Message ???
            } else {
                disableConfigRegionRunnable.run();
                hbox.getChildren().set(0, pauseButton); // Replace runButton with pauseButton

                canvasRenderer.draw();
                pauseButton.setDisable(false);
                cancelButton.setDisable(false);
                createAndPlayTimeline(canvasRenderer, actionAfterSimulationStopped);
            }
        });
        resumeButton.setOnAction(event -> {
            stopTimeline();
            startButton.setDisable(true);
            resumeButton.setDisable(true);
            pauseButton.setDisable(true);
            cancelButton.setDisable(true);
            speedSlider.setDisable(true);
            disableConfigRegionRunnable.run();
            hbox.getChildren().set(0, pauseButton); // Replace resumeButton with pauseButton
            pauseButton.setDisable(false);
            cancelButton.setDisable(false);
            createAndPlayTimeline(canvasRenderer, actionAfterSimulationStopped);
        });
        pauseButton.setOnAction(event -> {
            stopTimeline();
            startButton.setDisable(true);
            resumeButton.setDisable(true);
            pauseButton.setDisable(true);
            cancelButton.setDisable(true);
            speedSlider.setDisable(true);
            disableConfigRegionRunnable.run();
            hbox.getChildren().set(0, resumeButton); // Replace pauseButton with runButton
            resumeButton.setDisable(false);
            cancelButton.setDisable(false);
            speedSlider.setDisable(false);
        });
        cancelButton.setOnAction(event -> {
            stopTimeline();
            startButton.setDisable(true);
            resumeButton.setDisable(true);
            pauseButton.setDisable(true);
            cancelButton.setDisable(true);
            speedSlider.setDisable(true);
            enableConfigRegionRunnable.run();
            hbox.getChildren().set(0, startButton); // Replace pauseButton with startButton
            startButton.setDisable(false);
            speedSlider.setDisable(false);
        });

        return hbox;
    }

    private Button buildControlButton(String text) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add("control-button");
        return controlButton;
    }

    private void createAndPlayTimeline(WaTorCanvasRenderer canvasRenderer, Runnable actionAfterSimulationStopped) {
        timeline = new Timeline(new KeyFrame(waTorModel.speedAsDuration(), event -> {
            boolean finished = updateSimulationSupplier.getAsBoolean();
            canvasRenderer.draw();
            if (finished) {
                stopTimeline();

                actionAfterSimulationStopped.run();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void stopTimeline() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    private Region createObservationRegion() {
        List<Label> labels = new ArrayList<>();
        labels.add(buildObservationPropertyLabel(waTorModel.speedProperty().asString("Speed: %d")));
        labels.add(buildObservationPropertyLabel(waTorModel.timeCounterProperty().asString("Time: %d")));
        labels.add(buildObservationPropertyLabel(waTorModel.cellLengthProperty().asString("Cell length: %d")));
        labels.add(buildObservationPropertyLabel(waTorModel.xSizeProperty().asString("X size: %d")));
        labels.add(buildObservationPropertyLabel(waTorModel.ySizeProperty().asString("Y size: %d")));
        labels.add(buildObservationPropertyLabel(waTorModel.fishNumberProperty().asString("Fish number: %d")));
        labels.add(buildObservationPropertyLabel(waTorModel.sharkNumberProperty().asString("Shark number: %d")));

        VBox vbox = new VBox();
        vbox.getChildren().addAll(labels);
        vbox.getStyleClass().add("observation-vbox");

        return vbox;
    }

    private Label buildObservationPropertyLabel(StringBinding stringBinding) {
        Label label = new Label();
        label.textProperty().bind(stringBinding);
        return label;
    }

}
