package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class DefaultControlView extends AbstractControlView<DefaultControlViewModel> {

    public DefaultControlView(DefaultControlViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildControlRegion() {
        // Button
        String textStart = AppLocalization.getText(AppLocalizationKeys.CONTROL_START);
        String textPause = AppLocalization.getText(AppLocalizationKeys.CONTROL_PAUSE);
        String textResume = AppLocalization.getText(AppLocalizationKeys.CONTROL_RESUME);
        String textCancel = AppLocalization.getText(AppLocalizationKeys.CONTROL_CANCEL);

        Button actionButton = createControlButton(textStart, false);
        Button cancelButton = createControlButton(textCancel, true);

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

        actionButton.setOnAction(_ -> viewModel.requestActionButton());
        cancelButton.setOnAction(_ -> viewModel.requestCancelButton());

        // Config
        var stepDurationControl = FXComponentFactory.createLabeledIntSlider(
                viewModel.stepDurationProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONTROL_STEP_DURATION),
                AppLocalization.getText(AppLocalizationKeys.CONTROL_STEP_DURATION_TOOLTIP), // TODO Adjust tooltip with min and max
                FXStyleClasses.CONFIG_SLIDER
        );

        stepDurationControl.controlRegion().disableProperty().bind(
                viewModel.simulationStateProperty().isEqualTo(SimulationState.RUNNING)
        );

        VBox stepDurationBox = new VBox(stepDurationControl.label(), stepDurationControl.controlRegion());
        stepDurationBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        return createControlMainBox(actionButton, cancelButton, stepDurationBox);
    }

}