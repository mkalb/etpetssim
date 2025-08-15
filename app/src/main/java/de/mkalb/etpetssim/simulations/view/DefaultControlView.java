package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class DefaultControlView
        extends AbstractControlView<DefaultControlViewModel> {

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
                Bindings.createStringBinding(() -> {
                    if (viewModel.getSimulationState().canStart()) {
                        return textStart;
                    } else if (viewModel.getSimulationState().isRunning()) {
                        return textPause;
                    } else if (viewModel.getSimulationState().isPaused()) {
                        return textResume;
                    } else {
                        return "...";
                    }
                }, viewModel.simulationStateProperty())
        );
        actionButton.disableProperty().bind(Bindings.createBooleanBinding(
                        () -> !viewModel.getSimulationState().canStart()
                                && !viewModel.getSimulationState().isRunning()
                                && !viewModel.getSimulationState().isPaused(),
                        viewModel.simulationStateProperty()
                )
        );
        cancelButton.disableProperty().bind(Bindings.createBooleanBinding(
                        () -> !viewModel.getSimulationState().isRunning()
                                && !viewModel.getSimulationState().isPaused(),
                        viewModel.simulationStateProperty()
                )
        );

        actionButton.setOnAction(_ -> viewModel.requestActionButton());
        cancelButton.setOnAction(_ -> viewModel.requestCancelButton());

        // Config
        var simulationModeControl = FXComponentFactory.createLabeledEnumRadioButtons(viewModel.simulationModeProperty(),
                viewModel.simulationModeProperty().displayNameProvider(),
                FXComponentFactory.createVBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                "Simulation Mode:", // TODO Label text
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_RADIOBUTTON
        );

        simulationModeControl.controlRegion().disableProperty().bind(Bindings.createBooleanBinding(
                        () -> viewModel.getSimulationState().isControlConfigDisabled(),
                        viewModel.simulationStateProperty()
                )
        );

        var stepDurationControl = FXComponentFactory.createLabeledIntSlider(
                viewModel.stepDurationProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONTROL_STEP_DURATION),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONTROL_STEP_DURATION_TOOLTIP,
                        viewModel.stepDurationProperty().min(),
                        viewModel.stepDurationProperty().max()),
                FXStyleClasses.CONFIG_SLIDER
        );

        stepDurationControl.controlRegion().disableProperty().bind(Bindings.createBooleanBinding(
                        () -> viewModel.getSimulationState().isControlConfigDisabled(),
                        viewModel.simulationStateProperty()
                )
        );

        VBox simulationModeBox = new VBox(simulationModeControl.label(), simulationModeControl.controlRegion());
        simulationModeBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox stepDurationBox = new VBox(stepDurationControl.label(), stepDurationControl.controlRegion());
        stepDurationBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        return createControlMainBox(actionButton, cancelButton, simulationModeBox, stepDurationBox);
    }

}
