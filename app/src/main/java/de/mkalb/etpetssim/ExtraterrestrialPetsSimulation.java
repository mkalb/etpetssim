package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.CommandLineArguments;
import de.mkalb.etpetssim.wator.WaTorController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class ExtraterrestrialPetsSimulation extends Application {

    @SuppressWarnings("CallToSystemExit")
    public static void main(String[] args) {
        CommandLineArguments commandLineArguments = new CommandLineArguments(args);
        if (commandLineArguments.getBoolean(CommandLineArguments.Key.HELP, false)) {
            commandLineArguments.printHelp();
            System.exit(0);
        }

        launch();
    }

    private Scene createWaTorScene() {
        return new Scene(new WaTorController().buildViewRegion());
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = createWaTorScene();

        primaryStage.setTitle("extraterrestrial pets simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
