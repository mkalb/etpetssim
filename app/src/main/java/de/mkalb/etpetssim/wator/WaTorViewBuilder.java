package de.mkalb.etpetssim.wator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
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

    private final WaTorConfigModel waTorConfigModel;
    private final WaTorSimulationModel waTorSimulationModel;
    private final BooleanSupplier startSimulationSupplier;
    private final BooleanSupplier updateSimulationSupplier;
    private final Function<WaTorCoordinate, Optional<WaTorCreature>> creatureFunction;
    private @Nullable Timeline timeline;

    public WaTorViewBuilder(WaTorConfigModel waTorConfigModel, WaTorSimulationModel waTorSimulationModel,

                            BooleanSupplier startSimulationSupplier,
                            BooleanSupplier updateSimulationSupplier, Function<WaTorCoordinate, Optional<WaTorCreature>> creatureFunction) {
        this.waTorConfigModel = waTorConfigModel;
        this.waTorSimulationModel = waTorSimulationModel;
        this.startSimulationSupplier = startSimulationSupplier;
        this.updateSimulationSupplier = updateSimulationSupplier;
        this.creatureFunction = creatureFunction;
    }

    @Override
    public Region build() {
        WaTorCanvasRenderer canvasRenderer = new WaTorCanvasRenderer(waTorConfigModel, waTorSimulationModel,
                creatureFunction);

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
        ChoiceBox<Integer> choiceBox = new ChoiceBox<>(waTorConfigModel.cellLengthChoices());
        choiceBox.valueProperty().bindBidirectional(waTorConfigModel.cellLengthProperty());

        Slider xSizeSlider = new Slider(WaTorConfigModel.MIN_SIZE, Math.round(2048 / waTorConfigModel.cellLength()), waTorConfigModel.xSize());
        initConfigSlider(xSizeSlider, 128 / waTorConfigModel.cellLength(), waTorConfigModel.xSizeProperty());

        Slider ySizeSlider = new Slider(WaTorConfigModel.MIN_SIZE, Math.round(2048 / waTorConfigModel.cellLength()), waTorConfigModel.ySize());
        initConfigSlider(ySizeSlider, 128 / waTorConfigModel.cellLength(), waTorConfigModel.ySizeProperty());

        Slider fishNumberSlider = new Slider(0.0d, Math.round(waTorConfigModel.xSize() * waTorConfigModel.ySize()) / 4, waTorConfigModel.fishNumber());
        initConfigSlider(fishNumberSlider, Math.round(waTorConfigModel.xSize() * waTorConfigModel.ySize()) / 32, waTorConfigModel.fishNumberProperty());

        Slider sharkNumberSlider = new Slider(0.0d, Math.round(waTorConfigModel.xSize() * waTorConfigModel.ySize()) / 8, waTorConfigModel.sharkNumber());
        initConfigSlider(sharkNumberSlider, Math.round(waTorConfigModel.xSize() * waTorConfigModel.ySize()) / 64, waTorConfigModel.sharkNumberProperty());

        // Listener
        choiceBox.valueProperty().addListener((obs, oldval, newVal) -> {
            xSizeSlider.setMax(Math.round(2048 / newVal.intValue()));
            ySizeSlider.setMax(Math.round(2048 / newVal.intValue()));
            xSizeSlider.setMajorTickUnit(128 / newVal.intValue());
            ySizeSlider.setMajorTickUnit(128 / newVal.intValue());
        });
        xSizeSlider.valueProperty().addListener((obs, oldval, newVal) -> {
            xSizeSlider.setValue(newVal.intValue());
            fishNumberSlider.setMax(Math.round(newVal.intValue() * waTorConfigModel.ySize()) / 4);
            sharkNumberSlider.setMax(Math.round(newVal.intValue() * waTorConfigModel.ySize()) / 8);
            fishNumberSlider.setMajorTickUnit(Math.round(newVal.intValue() * waTorConfigModel.ySize()) / 32);
            sharkNumberSlider.setMajorTickUnit(Math.round(newVal.intValue() * waTorConfigModel.ySize()) / 64);
        });
        ySizeSlider.valueProperty().addListener((obs, oldval, newVal) -> {
            ySizeSlider.setValue(newVal.intValue());
            fishNumberSlider.setMax(Math.round(waTorConfigModel.xSize() * newVal.intValue()) / 4);
            sharkNumberSlider.setMax(Math.round(waTorConfigModel.xSize() * newVal.intValue()) / 8);
            fishNumberSlider.setMajorTickUnit(Math.round(waTorConfigModel.xSize() * newVal.intValue()) / 32);
            sharkNumberSlider.setMajorTickUnit(Math.round(waTorConfigModel.xSize() * newVal.intValue()) / 64);
        });
        fishNumberSlider.valueProperty().addListener((obs, oldval, newVal) ->
                fishNumberSlider.setValue(newVal.intValue()));
        sharkNumberSlider.valueProperty().addListener((obs, oldval, newVal) ->
                sharkNumberSlider.setValue(newVal.intValue()));

        Label cellLengthLabel = buildPropertyLabel(Bindings.createStringBinding(
                () -> String.format("%d", waTorConfigModel.cellLengthProperty().getValue()),
                waTorConfigModel.cellLengthProperty()));
        Label xSizeLabel = buildPropertyLabel(waTorConfigModel.xSizeProperty().asString("%d"));
        Label ySizeLabel = buildPropertyLabel(waTorConfigModel.ySizeProperty().asString("%d"));
        Label fishNumberLabel = buildPropertyLabel(waTorConfigModel.fishNumberProperty().asString("%d"));
        Label sharkNumberLabel = buildPropertyLabel(waTorConfigModel.sharkNumberProperty().asString("%d"));

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

        gridPane.add(cellLengthLabel, 2, 0);
        gridPane.add(xSizeLabel, 2, 1);
        gridPane.add(ySizeLabel, 2, 2);
        gridPane.add(fishNumberLabel, 2, 3);
        gridPane.add(sharkNumberLabel, 2, 4);

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
        Button startButton = buildControlButton("Start", false);
        Button resumeButton = buildControlButton("Resume", true);
        Button pauseButton = buildControlButton("Pause", true);
        Button cancelButton = buildControlButton("Cancel", true);

        Label speedLabel = new Label("Speed");

        Slider speedSlider = new Slider(5.0d, 100.0d, waTorConfigModel.speedAsHundredthOfASecond());
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(5.0d);
        speedSlider.setMinorTickCount(0);
        speedSlider.setBlockIncrement(1.0d);
        speedSlider.valueProperty().bindBidirectional(waTorConfigModel.speedProperty());
        speedSlider.valueProperty().addListener((obs, oldval, newVal) ->
                speedSlider.setValue(newVal.intValue()));
        speedSlider.getStyleClass().add("control-slider");

        HBox hbox = new HBox();
        hbox.getChildren().addAll(startButton, cancelButton, speedLabel, speedSlider);
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
            canvasRenderer.draw();

            if (finished) {
                startButton.setDisable(false);
                speedSlider.setDisable(false);
                // TODO Show Message ???
            } else {
                disableConfigRegionRunnable.run();
                hbox.getChildren().set(0, pauseButton); // Replace runButton with pauseButton

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

    private Button buildControlButton(String text, boolean disabled) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add("control-button");
        controlButton.setDisable(disabled);
        return controlButton;
    }

    private void createAndPlayTimeline(WaTorCanvasRenderer canvasRenderer, Runnable actionAfterSimulationStopped) {
        timeline = new Timeline(new KeyFrame(waTorConfigModel.speedAsDuration(), event -> {
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
        labels.add(buildPropertyLabel(waTorSimulationModel.timeCounterProperty().asString("Time: %d")));
        labels.add(buildPropertyLabel(waTorSimulationModel.fishNumberProperty().asString("Fish: %d")));
        labels.add(buildPropertyLabel(waTorSimulationModel.sharkNumberProperty().asString("Shark: %d")));

        VBox vbox = new VBox();
        vbox.getChildren().addAll(labels);
        vbox.getStyleClass().add("observation-vbox");

        return vbox;
    }

    private Label buildPropertyLabel(StringBinding stringBinding) {
        Label label = new Label();
        label.textProperty().bind(stringBinding);
        return label;
    }

}
