package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.simulations.SimulationFactory;
import de.mkalb.etpetssim.simulations.SimulationType;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * The main class for the Extraterrestrial Pets Simulation application.
 * It initializes the application, sets up logging, localization, and starts the JavaFX application.
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
     * The main entry point for the Extraterrestrial Pets Simulation application.
     * The command-line arguments are parsed with AppArgs.
     * Use "--help" to display the help message and exit the application.
     *
     * @param args the command-line arguments passed to the application
     */
    public static void main(String[] args) {
        var arguments = parseArgumentsAndHandleHelp(args);
        initAppLogger(arguments);
        initializeAppLocalization(arguments);

        AppLogger.info("Application is launching with arguments: " + arguments.argumentsAsString());
        launch(args);
    }

    /**
     * Parses command-line arguments and handles the help flag.
     * If the help flag is active, it prints the help message and exits the application.
     *
     * @param args the command-line arguments passed to the application
     * @return an instance of AppArgs containing the parsed arguments
     */
    @SuppressWarnings("CallToSystemExit")
    private static AppArgs parseArgumentsAndHandleHelp(String[] args) {
        AppArgs arguments = new AppArgs(args);
        if (arguments.isFlagActive(AppArgs.Key.HELP)) {
            AppArgs.Key.printHelp(System.out);
            // Exit the JavaFX application after printing help
            AppLogger.info("Exiting application after printing help.");
            System.exit(0);
        }
        return arguments;
    }

    /**
     * Initializes the application logger based on command-line arguments.
     * @param arguments the parsed command-line arguments
     */
    private static void initAppLogger(AppArgs arguments) {
        var logLevel = arguments.getValue(AppArgs.Key.LOG_LEVEL)
                                .flatMap(AppLogger.LogLevel::fromString)
                                .orElse(AppLogger.DEFAULT_LOG_LEVEL);
        boolean useConsole = arguments.getBoolean(AppArgs.Key.LOG_CONSOLE, false);
        try {
            boolean useFile = arguments.getBoolean(AppArgs.Key.LOG_FILE, false);
            Path logPath = useFile ? AppStorage.getLogFile(AppLogger.LOG_FILE_NAME, AppStorage.OperatingSystem.detect()) : null;

            // Initialize the AppLogger with the specified log level, console usage, and log file path
            AppLogger.initialize(logLevel, useConsole, logPath);
        } catch (IOException e) {
            // Initialize the AppLogger with the specified log level, console usage and null log file path
            AppLogger.initialize(logLevel, useConsole, null);
            AppLogger.error("Failed to initialize log file.", e);
        }
    }

    /**
     * Initializes the application localization based on command-line arguments.
     * Set the default locale for the application.
     *
     * @param arguments the parsed command-line arguments
     */
    private static void initializeAppLocalization(AppArgs arguments) {
        AppLocalization.initialize(arguments.getValue(AppArgs.Key.LOCALE).orElse(null));
        Locale.setDefault(AppLocalization.locale());
    }

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
            AppLogger.error("Failed to load application icons. Icons will not be set.");
        } else {
            stage.getIcons().addAll(icons);
        }
    }

    /**
     * Updates the JavaFX stage with a new scene based on the specified simulation type.
     * It is passed as a method reference to the SimulationFactory and the StartView.
     *
     * @param stage the JavaFX stage to update
     * @param simulationType the type of simulation to display in the new scene
     * @see de.mkalb.etpetssim.simulations.SimulationFactory
     * @see de.mkalb.etpetssim.simulations.start.StartView
     */
    void updateStageScene(Stage stage, SimulationType simulationType) {
        Objects.requireNonNull(stage, "Stage must not be null");
        Objects.requireNonNull(simulationType, "SimulationType must not be null");

        // Scene with a VBox
        VBox vBox = new VBox();
        vBox.getChildren().add(buildSimulationHeaderNode(simulationType));
        var instance = SimulationFactory.createInstance(simulationType, stage, this::updateStageScene);
        stage.setOnCloseRequest(_ -> {
            AppLogger.info("Shutting down simulation instance: " + instance.simulationType());
            instance.simulationMainView().shutdownSimulation();
        });
        vBox.getChildren().add(instance.region());
        vBox.getStyleClass().add(FXStyleClasses.APP_VBOX);
        Scene scene = new Scene(vBox);

        // Add common stylesheets first and then the specific simulation type stylesheet
        AppResources.getCss("scene.css").ifPresent(scene.getStylesheets()::add);
        simulationType.cssResource().ifPresent(scene.getStylesheets()::add);

        // Stage
        stage.setTitle(AppLocalization.getText("window.title") + " - " + simulationType.title());
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
     * Builds the header node for the simulation, which includes the title, subtitle, and URL link if available.
     *
     * @param simulationType the type of simulation to create the header for
     * @return a Node representing the header of the simulation
     */
    private Node buildSimulationHeaderNode(SimulationType simulationType) {
        VBox simulationHeaderBox = new VBox(FXComponentFactory.createLabel(simulationType.title(), FXStyleClasses.HEADER_TITLE_LABEL));
        simulationHeaderBox.getStyleClass().add(FXStyleClasses.HEADER_VBOX);

        simulationType.subtitle()
                      .ifPresent(subtitle -> simulationHeaderBox.getChildren().add(FXComponentFactory.createLabel(subtitle, FXStyleClasses.HEADER_SUBTITLE_LABEL)));

        simulationType.urlAsURI().ifPresent(url -> {
            Hyperlink urlLink = new Hyperlink(url.toString());
            urlLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
            urlLink.setOnAction(e -> getHostServices().showDocument(url.toString()));
            simulationHeaderBox.getChildren().add(urlLink);
        });

        return simulationHeaderBox;
    }

    @Override
    public void start(Stage primaryStage) {
        // Determine the simulation type from command-line arguments
        AppArgs arguments = new AppArgs(getParameters().getRaw().toArray(new String[0]));
        SimulationType type = determineSimulationType(arguments, true)
                .orElse(SimulationType.STARTSCREEN); // Default to STARTSCREEN if no valid simulation type is found
        AppLogger.info("Application is starting with simulation type: " + type.name());

        // Initialize and show the primary stage with the appropriate scene
        updateStageIcons(primaryStage);
        updateStageScene(primaryStage, type);
        primaryStage.show();
    }

    @Override
    public void stop() {
        AppLogger.info("Application is shutting down.");
        AppLogger.shutdown();
    }

}
