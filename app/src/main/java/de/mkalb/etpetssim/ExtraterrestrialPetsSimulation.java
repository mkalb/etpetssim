package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.simulations.SimulationFactory;
import de.mkalb.etpetssim.simulations.SimulationType;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.*;

/**
 * The main JavaFX {@link Application} class for the Extraterrestrial Pets Simulation.
 * <p>
 * Responsible for setting up the user interface, handling simulation selection,
 * and managing the application lifecycle events such as startup and shutdown.
 * Command-line argument parsing, logging, and localization are initialized in {@link AppLauncher}.
 * </p>
 */
public final class ExtraterrestrialPetsSimulation extends Application {

    /**
     * The application icon images used in the JavaFX application.
     */
    private static final String[] APP_IMAGES = {
            "etpetssim16.png",
            "etpetssim32.png",
            "etpetssim64.png",
            "etpetssim128.png"
    };

    /**
     * Determines the simulation type based on the command-line arguments.
     *
     * @param arguments the parsed application arguments
     * @param onlyImplemented if true, only implemented simulations are considered
     * @return an Optional containing the matching SimulationType, or empty if none found
     */
    @SuppressWarnings("SameParameterValue")
    private Optional<SimulationType> determineSimulationType(AppArgs arguments, boolean onlyImplemented) {
        return arguments.getValue(AppArgs.Key.SIMULATION)
                        .flatMap(arg -> SimulationType.fromCliArgument(arg, onlyImplemented));
    }

    /**
     * Updates the JavaFX stage with the application icons.
     *
     * @param stage the JavaFX stage to update with icons
     */
    private void updateStageIcons(Stage stage) {
        List<Image> icons = AppResources.getImages(APP_IMAGES);
        if (icons.isEmpty()) {
            AppLogger.error("Application: Failed to load application icons. Icons will not be set.");
        } else {
            stage.getIcons().addAll(icons);
        }
    }

    /**
     * Updates the JavaFX stage with a new scene based on the specified simulation type.
     * It is passed as a method reference to the SimulationFactory and the StartMainView.
     *
     * @param stage the JavaFX stage to update
     * @param simulationType the type of simulation to display in the new scene
     * @see de.mkalb.etpetssim.simulations.SimulationFactory
     * @see de.mkalb.etpetssim.simulations.start.StartMainView
     */
    void updateStageScene(Stage stage, SimulationType simulationType) {
        Objects.requireNonNull(stage, "Stage must not be null");
        Objects.requireNonNull(simulationType, "SimulationType must not be null");

        // Scene with a VBox
        VBox vBox = new VBox();
        vBox.getChildren().add(buildSimulationHeaderNode(simulationType));
        var instance = SimulationFactory.createInstance(simulationType, stage, this::updateStageScene);
        stage.setOnCloseRequest(_ -> {
            AppLogger.info("Application: Shutting down simulation instance: " + instance.simulationType());
            instance.simulationMainView().shutdownSimulation();
        });
        Region instanceRegion = instance.region();
        vBox.getChildren().add(instanceRegion);
        vBox.getStyleClass().add(FXStyleClasses.APP_VBOX);
        VBox.setVgrow(instanceRegion, Priority.ALWAYS);
        Scene scene = new Scene(vBox);

        // Add common stylesheets first and then the specific simulation type stylesheet
        AppResources.getCss("scene.css").ifPresent(scene.getStylesheets()::add);
        simulationType.cssResource().ifPresent(scene.getStylesheets()::add);

        // Stage
        stage.setTitle(AppLocalization.getFormattedText(AppLocalizationKeys.WINDOW_TITLE, simulationType.title()));
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        // Ensure the window does not exceed the screen width and height
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxWidth(screenBounds.getWidth());
        stage.setMaxHeight(screenBounds.getHeight());
        if (stage.getWidth() > screenBounds.getWidth()) {
            stage.setWidth(screenBounds.getWidth());
            stage.setX(screenBounds.getMinX());
        }
        if (stage.getHeight() > screenBounds.getHeight()) {
            stage.setHeight(screenBounds.getHeight());
            stage.setY(screenBounds.getMinY());
        }
    }

    /**
     * Builds the header node for the simulation, which includes the title, subtitle, and links.
     *
     * @param simulationType the type of simulation to create the header for
     * @return a Node representing the header of the simulation
     */
    private Node buildSimulationHeaderNode(SimulationType simulationType) {
        VBox titleBox = new VBox(FXComponentFactory.createLabel(simulationType.title(), FXStyleClasses.HEADER_TITLE_LABEL));
        titleBox.getStyleClass().add(FXStyleClasses.HEADER_TITLE_VBOX);

        simulationType.subtitle()
                      .ifPresent(subtitle ->
                              titleBox.getChildren().add(FXComponentFactory.createLabel(subtitle, FXStyleClasses.HEADER_SUBTITLE_LABEL)));

        VBox linkBox = new VBox();
        linkBox.getStyleClass().add(FXStyleClasses.HEADER_LINK_VBOX);

        Hyperlink aboutLink = new Hyperlink(AppLocalization.getText(AppLocalizationKeys.HEADER_ABOUT_LINK));
        aboutLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
        aboutLink.setOnAction(_ -> new AboutDialog(AppResources.getImages(APP_IMAGES)).showAboutDialog());
        linkBox.getChildren().add(aboutLink);

        simulationType.urlAsURI().ifPresent(url -> {
            Hyperlink urlLink = new Hyperlink(url.toString());
            urlLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
            urlLink.setOnAction(_ -> getHostServices().showDocument(url.toString()));
            linkBox.getChildren().add(urlLink);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox simulationHeaderBox = new HBox(titleBox, spacer, linkBox);
        simulationHeaderBox.getStyleClass().add(FXStyleClasses.HEADER_HBOX);

        return simulationHeaderBox;
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize exception handling for uncaught exceptions
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unexpected Error");
                alert.setHeaderText("An unexpected error occurred.");
                alert.setContentText(throwable.getMessage());
                alert.showAndWait();
            });
            AppLogger.error("Uncaught exception in thread " + thread.getName(), throwable);
        });

        // Determine the simulation type from command-line arguments
        AppArgs arguments = new AppArgs(getParameters().getRaw().toArray(new String[0]));
        SimulationType type = determineSimulationType(arguments, true)
                .orElse(SimulationType.STARTSCREEN); // Default to STARTSCREEN if no valid simulation type is found
        AppLogger.info("Application: Starting with simulation type: " + type.name());

        // Initialize and show the primary stage with the appropriate scene
        updateStageIcons(primaryStage);
        updateStageScene(primaryStage, type);
        primaryStage.show();
    }

    @Override
    public void stop() {
        AppLogger.info("Application: Shutting down.");
        AppLogger.shutdown();
    }

}
