package de.mkalb.etpetssim.simulations.conwayslife.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayControlViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public final class ConwayControlView {

    private final ConwayControlViewModel viewModel;

    public ConwayControlView(ConwayControlViewModel viewModel) {
        this.viewModel = viewModel;
    }

    Region buildRegion() {
        String textStart = AppLocalization.getText(AppLocalizationKeys.CONTROL_START);
        String textPause = AppLocalization.getText(AppLocalizationKeys.CONTROL_PAUSE);
        String textResume = AppLocalization.getText(AppLocalizationKeys.CONTROL_RESUME);
        String textCancel = AppLocalization.getText(AppLocalizationKeys.CONTROL_CANCEL);

        Button actionButton = buildControlButton(textStart, false);
        Button cancelButton = buildControlButton(textCancel, true);

        actionButton.textProperty().bind(
                Bindings.createStringBinding(() -> switch (viewModel.getSimulationState()) {
                    case READY -> textStart;
                    case RUNNING -> textPause;
                    case PAUSED -> textResume;
                }, viewModel.simulationStateProperty())
        );
        cancelButton.disableProperty().bind(
                viewModel.simulationStateProperty().isEqualTo(SimulationState.READY)
        );

        actionButton.setOnAction(_ -> viewModel.onActionButtonClicked());
        cancelButton.setOnAction(_ -> viewModel.onCancelButtonClicked());

        HBox hbox = new HBox();
        hbox.getChildren().addAll(actionButton, cancelButton);
        hbox.getStyleClass().add(FXStyleClasses.CONTROL_HBOX);

        return hbox;
    }

    private Button buildControlButton(String text, boolean disabled) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);
        controlButton.setDisable(disabled);
        return controlButton;
    }

}