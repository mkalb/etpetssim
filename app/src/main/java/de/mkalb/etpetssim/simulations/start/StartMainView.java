package de.mkalb.etpetssim.simulations.start;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.SimulationType;
import de.mkalb.etpetssim.simulations.view.SimulationControlView;
import de.mkalb.etpetssim.simulations.view.SimulationMainView;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.*;

/**
 * Main view for the start screen of the Extraterrestrial Pets Simulation application.
 * <p>
 * Implements {@link de.mkalb.etpetssim.simulations.view.SimulationMainView} and builds the UI region containing buttons
 * for each available simulation type. This class encapsulates both the view and
 * its logic, as no separate ViewModel or Model is required for the start screen.
 * </p>
 */
public final class StartMainView implements SimulationMainView, SimulationControlView {

    private final Stage stage;
    private final BiConsumer<Stage, SimulationType> stageUpdater;

    public StartMainView(Stage stage,
                         BiConsumer<Stage, SimulationType> stageUpdater) {
        this.stage = stage;
        this.stageUpdater = stageUpdater;
    }

    @Override
    public Region buildMainRegion() {
        Region controlRegion = buildControlRegion();

        BorderPane borderPane = new BorderPane();
        borderPane.setBottom(controlRegion);
        borderPane.getStyleClass().add(FXStyleClasses.MAIN_BORDERPANE);

        return borderPane;
    }

    @Override
    public void shutdownSimulation() {
        // Do nothing
    }

    @Override
    public Region buildControlRegion() {
        VBox vbox = new VBox();
        vbox.getStyleClass().add(FXStyleClasses.START_CONTROL_VBOX);

        Label titleLabel = FXComponentFactory.createLabel(AppLocalization.getText(SimulationType.labelResourceKey()),
                FXStyleClasses.START_TITLE_LABEL);
        vbox.getChildren().add(titleLabel);

        for (SimulationType type : SimulationType.values()) {
            if (!type.isShownOnStartScreen()) {
                continue;
            }

            Button button = new Button(type.title());
            button.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);
            if (type.isImplemented()) {
                type.subtitle().ifPresent(subtitle -> button.setTooltip(new Tooltip(subtitle)));
                button.setOnAction(_ -> stageUpdater.accept(stage, type));
            } else {
                button.setDisable(true);
            }

            vbox.getChildren().add(button);
        }

        return vbox;
    }

}
