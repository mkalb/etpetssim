package de.mkalb.etpetssim.simulations.startscreen;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.SimulationType;
import de.mkalb.etpetssim.simulations.SimulationView;
import de.mkalb.etpetssim.ui.FXComponentBuilder;
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
 * Implements {@link SimulationView} and builds the UI region containing buttons
 * for each available simulation type. This class encapsulates both the view and
 * its logic, as no separate ViewModel or Model is required for the start screen.
 * </p>
 */
public final class StartScreenView implements SimulationView {

    private final Stage stage;
    private final BiConsumer<Stage, SimulationType> stageUpdater;

    public StartScreenView(Stage stage,
                           BiConsumer<Stage, SimulationType> stageUpdater) {
        this.stage = stage;
        this.stageUpdater = stageUpdater;
    }

    @Override
    public Region buildViewRegion() {
        VBox root = new VBox();

        Label titleLabel = FXComponentBuilder.createLabel(AppLocalization.getText(SimulationType.labelResourceKey()), "startscreen-title-label");
        root.getChildren().add(titleLabel);

        for (SimulationType type : SimulationType.values()) {
            if (!type.isShownOnStartScreen()) {
                continue;
            }

            Button button = new Button(type.title());
            if (type.isImplemented()) {
                type.subtitle().ifPresent(subtitle -> button.setTooltip(new Tooltip(subtitle)));
                button.setOnAction(_ -> stageUpdater.accept(stage, type));
            } else {
                button.setDisable(true);
            }

            root.getChildren().add(button);
        }

        return root;
    }

}
