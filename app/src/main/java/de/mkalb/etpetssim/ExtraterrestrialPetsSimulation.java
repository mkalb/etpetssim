package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.wator.WaTorController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.*;

public final class ExtraterrestrialPetsSimulation extends Application {

    @SuppressWarnings("CallToSystemExit")
    public static void main(String[] args) {
        // Parse command-line arguments
        CommandLineArguments arguments = new CommandLineArguments(args);
        if (arguments.isFlagActive(CommandLineArguments.Key.HELP)) {
            CommandLineArguments.Key.printHelp(System.out);
            // Exit the JavaFX application after printing help
            System.exit(0);
        }

        // Initialize the LoggingManager
        // TODO Add arguments for log level and log handler (console or file)
        LoggingManager.initialize(LoggingManager.LogLevel.INFO, true, null);

        // Initialize the LanguageManager
        LanguageManager.initialize(arguments.getValue(CommandLineArguments.Key.LOCALE).orElse(null));
        LoggingManager.info("Starting Extraterrestrial Pets Simulation with locale: " + LanguageManager.locale());

        // Start the JavaFX application
        launch();
    }

    private Scene createWaTorScene() {
        return new Scene(new WaTorController().buildViewRegion());
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = createWaTorScene();

        primaryStage.setTitle(LanguageManager.getText("window.title"));
        List<Image> images = ResourceLoader.getImages("etpetssim16.png", "etpetssim32.png", "etpetssim64.png");
        if (images.isEmpty()) {
            LoggingManager.error("Failed to load application icons. Icons will not be set.");
        } else {
            primaryStage.getIcons().addAll(images);
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
