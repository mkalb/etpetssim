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
        AppArgs arguments = new AppArgs(args);
        if (arguments.isFlagActive(AppArgs.Key.HELP)) {
            AppArgs.Key.printHelp(System.out);
            // Exit the JavaFX application after printing help
            System.exit(0);
        }

        // Initialize the AppLogger
        // TODO Add arguments for log level and log handler (console or file)
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);

        // Initialize the AppLocalization
        AppLocalization.initialize(arguments.getValue(AppArgs.Key.LOCALE).orElse(null));
        AppLogger.info("Starting Extraterrestrial Pets Simulation with locale: " + AppLocalization.locale());

        // Start the JavaFX application
        launch();
    }

    private Scene createWaTorScene() {
        return new Scene(new WaTorController().buildViewRegion());
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = createWaTorScene();

        primaryStage.setTitle(AppLocalization.getText("window.title"));
        List<Image> images = AppResources.getImages("etpetssim16.png", "etpetssim32.png", "etpetssim64.png");
        if (images.isEmpty()) {
            AppLogger.error("Failed to load application icons. Icons will not be set.");
        } else {
            primaryStage.getIcons().addAll(images);
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
