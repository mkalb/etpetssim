package de.mkalb.etpetssim;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public final class ExtraterrestrialPetsSimulation extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new Label("Hello"));

        primaryStage.setTitle("extraterrestrial pets simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
