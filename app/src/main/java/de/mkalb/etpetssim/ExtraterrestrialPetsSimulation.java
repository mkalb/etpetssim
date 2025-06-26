package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.wator.WaTorController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

    public static void main(String[] args) {
        var arguments = parseArgumentsAndHandleHelp(args);
        initAppLogger(arguments);
        initializeAppLocalization(arguments);

        AppLogger.info("Launching application");
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
     * @param arguments the parsed command-line arguments
     */
    private static void initializeAppLocalization(AppArgs arguments) {
        AppLocalization.initialize(arguments.getValue(AppArgs.Key.LOCALE).orElse(null));
    }

    private Scene createWaTorScene() {
        return new Scene(new WaTorController().buildViewRegion());
    }

    @Override
    public void start(Stage primaryStage) {
        AppLogger.info("Starting Extraterrestrial Pets Simulation application");

        // Parse command-line arguments
        var arguments = new AppArgs(getParameters().getRaw().toArray(new String[0]));

        // Choose the scene based on the command-line arguments
        Scene scene = null;
        Optional<String> simArg = arguments.getValue(AppArgs.Key.SIMULATION);
        if (simArg.isPresent()) {
            String simulationName = simArg.get().toLowerCase();
            // TODO Choose the simulation based on the argument
            scene = createWaTorScene();
        } else {
            AppLogger.warn("No simulation specified. Defaulting to WaTor simulation.");
            scene = createWaTorScene();
        }

        // Initialize the primary stage
        primaryStage.setTitle(AppLocalization.getText("window.title"));
        List<Image> images = AppResources.getImages(APP_IMAGES);
        if (images.isEmpty()) {
            AppLogger.error("Failed to load application icons. Icons will not be set.");
        } else {
            primaryStage.getIcons().addAll(images);
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        AppLogger.info("Application is shutting down.");
        AppLogger.shutdown();
    }

}
