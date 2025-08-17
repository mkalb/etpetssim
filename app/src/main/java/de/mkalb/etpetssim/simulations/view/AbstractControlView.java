package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.viewmodel.SimulationControlViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.*;

public abstract class AbstractControlView<VM extends SimulationControlViewModel>
        implements SimulationControlView {

    protected final VM viewModel;

    protected AbstractControlView(VM viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public Region buildControlRegion() {
        HBox mainBox = new HBox();
        mainBox.getStyleClass().add(FXStyleClasses.CONTROL_HBOX);

        // Button section (always present)
        Region buttonRegion = createControlButtonRegion();
        mainBox.getChildren().add(buttonRegion);

        // Config section (optional)
        createControlConfigRegion().ifPresent(configRegion -> mainBox.getChildren().add(configRegion));

        // Spacer before observation section (if present)
        Optional<Region> observationRegion = createControlObservationRegion();
        if (observationRegion.isPresent()) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            mainBox.getChildren().add(spacer);
            mainBox.getChildren().add(observationRegion.get());
        }

        return mainBox;
    }

    protected abstract Region createControlButtonRegion();

    protected abstract Optional<Region> createControlConfigRegion();

    protected abstract Optional<Region> createControlObservationRegion();

    protected final Button createControlButton(String text, boolean disabled) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);
        controlButton.setDisable(disabled);
        return controlButton;
    }

}
