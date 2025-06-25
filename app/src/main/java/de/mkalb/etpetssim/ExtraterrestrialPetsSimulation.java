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
        launch();
    }

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

    private static void initializeAppLocalization(AppArgs arguments) {
        AppLocalization.initialize(arguments.getValue(AppArgs.Key.LOCALE).orElse(null));
    }

    private Scene createWaTorScene() {
        return new Scene(new WaTorController().buildViewRegion());
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = createWaTorScene();

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

}
