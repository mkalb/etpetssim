package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.model.SimulationMode;
import de.mkalb.etpetssim.simulations.model.SimulationStartMode;
import de.mkalb.etpetssim.simulations.model.SimulationTerminationCheck;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.*;

public final class DefaultControlView
        extends AbstractControlView<DefaultControlViewModel> {

    private final Label stepNumberLabel = new Label();

    public DefaultControlView(DefaultControlViewModel viewModel) {
        super(viewModel);
    }

    @Override
    protected Pane createControlButtonPane() {
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

        HBox buttonBox = new HBox(actionButton, cancelButton);
        buttonBox.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON_HBOX);
        return buttonBox;
    }

    @Override
    protected Optional<Pane> createControlConfigPane() {
        var simulationModeControl = FXComponentFactory.createLabeledEnumRadioButtons(viewModel.simulationModeProperty(),
                viewModel.simulationModeProperty().displayNameProvider(),
                FXComponentFactory.createVBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                AppLocalization.getText(SimulationMode.labelResourceKey()),
                AppLocalization.getText(AppLocalizationKeys.CONTROL_SIMULATION_MODE_TOOLTIP),
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

        var startModeControl = FXComponentFactory.createLabeledEnumCheckBox(viewModel.startModeProperty(),
                SimulationStartMode.PAUSED,
                SimulationStartMode.RUNNING,
                AppLocalization.getText(AppLocalizationKeys.CONTROL_START_PAUSED),
                AppLocalization.getText(AppLocalizationKeys.CONTROL_START_PAUSED_TOOLTIP),
                FXStyleClasses.CONFIG_CHECKBOX
        );

        startModeControl.controlRegion().disableProperty().bind(Bindings.createBooleanBinding(
                        () -> viewModel.getSimulationState().cannotStart(),
                        viewModel.simulationStateProperty()
                )
        );

        var terminationCheckControl = FXComponentFactory.createLabeledEnumCheckBox(viewModel.terminationCheckProperty(),
                SimulationTerminationCheck.CHECKED,
                SimulationTerminationCheck.UNCHECKED,
                AppLocalization.getText(AppLocalizationKeys.CONTROL_TERMINATION_CHECK),
                AppLocalization.getText(AppLocalizationKeys.CONTROL_TERMINATION_CHECK_TOOLTIP),
                FXStyleClasses.CONFIG_CHECKBOX
        );

        terminationCheckControl.controlRegion().disableProperty().bind(Bindings.createBooleanBinding(
                        () -> viewModel.getSimulationState().cannotStart(),
                        viewModel.simulationStateProperty()
                )
        );

        VBox simulationModeBox = new VBox(simulationModeControl.label(), simulationModeControl.controlRegion());
        simulationModeBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox stepDurationBox = new VBox(stepDurationControl.label(), stepDurationControl.controlRegion());
        stepDurationBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox stepCountBox = new VBox(stepCountControl.label(), stepCountControl.controlRegion());
        stepCountBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox startModeBox = new VBox(startModeControl.label(), startModeControl.controlRegion());
        startModeBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox terminationCheckBox = new VBox(terminationCheckControl.label(), terminationCheckControl.controlRegion());
        terminationCheckBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        // Show stepDurationBox only in TIMED mode
        stepDurationBox.visibleProperty().bind(viewModel.simulationModeProperty().property().isEqualTo(SimulationMode.TIMED));
        stepDurationBox.managedProperty().bind(stepDurationBox.visibleProperty());

        // Show stepCountBox only in BATCH mode (not TIMED mode)
        stepCountBox.visibleProperty().bind(viewModel.simulationModeProperty().property().isNotEqualTo(SimulationMode.TIMED));
        stepCountBox.managedProperty().bind(stepCountBox.visibleProperty());

        // Place both boxes in a StackPane
        StackPane stepConfigPane = new StackPane(stepDurationBox, stepCountBox);

        HBox configBox = new HBox(simulationModeBox, stepConfigPane, startModeBox, terminationCheckBox);
        configBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_HBOX);
        return Optional.of(configBox);
    }

    @Override
    protected Optional<Pane> createControlObservationPane() {
        Label stepTitleLabel = new Label(AppLocalization.getText(AppLocalizationKeys.CONTROL_STEP_TITLE));

        VBox observationBox = new VBox(stepTitleLabel, stepNumberLabel);
        observationBox.getStyleClass().add(FXStyleClasses.CONTROL_OBSERVATION_VBOX);
        return Optional.of(observationBox);
    }

    public void updateStepCount(int stepCount) {
        stepNumberLabel.setText(AppLocalization.getFormattedText(AppLocalizationKeys.CONTROL_STEP_NUMBER, stepCount));
    }

}
