package de.mkalb.etpetssim.simulations.start;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.simulations.core.view.SimulationControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.*;

/**
 * Main view for the start screen of the Extraterrestrial Pets Simulation application.
 * <p>
 * Implements {@link SimulationMainView} and {@link SimulationControlView}. It builds a vertical list of
 * buttons for each {@link SimulationType} that is configured to be shown on the start screen. Implemented
 * simulations are clickable and switch the stage to the selected type; unimplemented ones are disabled.
 * This class encapsulates both the view and minimal control behavior; no dedicated ViewModel is required.
 * </p>
 */
@SuppressWarnings("ClassCanBeRecord")
public final class StartMainView implements SimulationMainView, SimulationControlView {

    private final Stage stage;
    private final BiConsumer<Stage, SimulationType> stageUpdater;

    /**
     * Creates a new start screen main view.
     *
     * @param stage the primary stage hosting the UI
     * @param stageUpdater a callback that switches the stage to a different simulation type
     */
    public StartMainView(Stage stage,
                         BiConsumer<Stage, SimulationType> stageUpdater) {
        this.stage = stage;
        this.stageUpdater = stageUpdater;
    }

    @Override
    public Region buildMainRegion() {
        return buildControlRegion();
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
