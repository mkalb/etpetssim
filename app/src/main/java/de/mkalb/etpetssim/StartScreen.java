package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Content of the start screen, which {@link EtpetssimApplication} shows below its header.
 *
 * <p>The start screen shows one button per {@link SimulationType} with a category. It only reports the selected
 * simulation type to its callback and holds no state.
 */
final class StartScreen {

    private StartScreen() {
    }

    /**
     * Creates the region of the start screen.
     *
     * @param simulationSelection receives the simulation type of a pressed button
     * @return a new region of the start screen
     */
    static Region createRegion(Consumer<SimulationType> simulationSelection) {
        VBox vbox = new VBox();
        vbox.getStyleClass().add(FXStyleClasses.START_CONTROL_VBOX);

        Label titleLabel = FXComponentFactory.createLabel(AppLocalization.getText(SimulationType.labelResourceKey()),
                FXStyleClasses.START_TITLE_LABEL);
        vbox.getChildren().add(titleLabel);

        for (SimulationType type : SimulationType.values()) {
            if (type.category().isEmpty()) {
                continue;
            }

            Button button = new Button(type.title());
            button.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);
            type.subtitle().ifPresent(subtitle -> button.setTooltip(new Tooltip(subtitle)));
            button.setOnAction(_ -> simulationSelection.accept(type));

            vbox.getChildren().add(button);
        }

        return vbox;
    }

}
