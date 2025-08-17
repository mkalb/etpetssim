package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.model.SimulationMode;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class DefaultControlView
        extends AbstractControlView<DefaultControlViewModel> {

    private final Label stepNumberLabel = new Label();

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

        var stepCountControl = FXComponentFactory.createLabeledIntSpinner(viewModel.stepCountProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONTROL_STEP_COUNT),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONTROL_STEP_COUNT_TOOLTIP,
                        viewModel.stepCountProperty().min(),
                        viewModel.stepCountProperty().max()),
                FXStyleClasses.CONFIG_SPINNER);

        stepCountControl.controlRegion().disableProperty().bind(Bindings.createBooleanBinding(
                        () -> viewModel.getSimulationState().isControlConfigDisabled(),
                        viewModel.simulationStateProperty()
                )
        );

        VBox simulationModeBox = new VBox(simulationModeControl.label(), simulationModeControl.controlRegion());
        simulationModeBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox stepDurationBox = new VBox(stepDurationControl.label(), stepDurationControl.controlRegion());
        stepDurationBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox stepCountBox = new VBox(stepCountControl.label(), stepCountControl.controlRegion());
        stepCountBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        // Place both boxes in a StackPane
        StackPane stepConfigPane = new StackPane(stepDurationBox, stepCountBox);

        // Show stepDurationBox only in LIVE mode
        stepDurationBox.visibleProperty().bind(viewModel.simulationModeProperty().property().isEqualTo(SimulationMode.LIVE));
        stepDurationBox.managedProperty().bind(stepDurationBox.visibleProperty());

        // Show stepCountBox only in BATCH mode
        stepCountBox.visibleProperty().bind(viewModel.simulationModeProperty().property().isEqualTo(SimulationMode.BATCH));
        stepCountBox.managedProperty().bind(stepCountBox.visibleProperty());

        // Step
        Label stepTitleLabel = new Label(AppLocalization.getText(AppLocalizationKeys.CONTROL_STEP_TITLE));
        VBox stepBox = new VBox(stepTitleLabel, stepNumberLabel);

        return createControlMainBox(actionButton, cancelButton, simulationModeBox, stepConfigPane, stepBox);
    }

    public void updateStepCount(int stepCount) {
        stepNumberLabel.setText(AppLocalization.getFormattedText(AppLocalizationKeys.CONTROL_STEP_NUMBER, stepCount));
    }

}
