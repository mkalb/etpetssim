package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.viewmodel.SimulationControlViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public abstract class AbstractControlView<VM extends SimulationControlViewModel> implements SimulationControlView {

    protected final VM viewModel;

    protected AbstractControlView(VM viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public abstract Region buildControlRegion();

    protected final Region createControlMainBox(Region... children) {
        HBox mainBox = new HBox();
        mainBox.getChildren().addAll(children);
        mainBox.getStyleClass().add(FXStyleClasses.CONTROL_HBOX);

        return mainBox;
    }

    protected final Button buildControlButton(String text, boolean disabled) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);
        controlButton.setDisable(disabled);
        return controlButton;
    }

}