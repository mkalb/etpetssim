package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.viewmodel.BaseControlViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public abstract class AbstractControlView<T extends BaseControlViewModel> implements BaseControlView {

    protected final T viewModel;

    protected AbstractControlView(T viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public abstract Region buildRegion();

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