package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.shared.SimulationMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStartMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationTerminationCheckMode;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Default JavaFX implementation of the simulation control view.
 */
public final class DefaultControlView implements SimulationControlView {

    private final DefaultControlViewModel viewModel;
    private final Label stepNumberLabel = new Label();

    public DefaultControlView(DefaultControlViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private static Button createControlButton(String text) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);
        return controlButton;
    }

    @Override
    public Region buildControlRegion() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox controlHBox = new HBox(createControlButtonPane(), createControlConfigPane(), spacer,
                createControlObservationPane());
        controlHBox.getStyleClass().add(FXStyleClasses.CONTROL_HBOX);

        ScrollPane controlScrollPane = new ScrollPane();
        controlScrollPane.getStyleClass().add(FXStyleClasses.CONTROL_SCROLLPANE);
        controlScrollPane.setContent(controlHBox);
        controlScrollPane.setFitToHeight(false);
        controlScrollPane.setFitToWidth(true);
        controlScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        controlScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        controlScrollPane.setPannable(false);

        return controlScrollPane;
    }

    @Override
    public void setStepCount(int stepCount) {
        stepNumberLabel.setText(
                AppLocalization.getFormattedText(SimulationLocalizationKeys.CONTROL_STEP_NUMBER, stepCount));
    }

    private Pane createControlButtonPane() {
        String textStart = AppLocalization.getText(SimulationLocalizationKeys.CONTROL_START);
        String textPause = AppLocalization.getText(SimulationLocalizationKeys.CONTROL_PAUSE);
        String textResume = AppLocalization.getText(SimulationLocalizationKeys.CONTROL_RESUME);
        String textCancel = AppLocalization.getText(SimulationLocalizationKeys.CONTROL_CANCEL);

        Button actionButton = createControlButton(textStart);
        Button cancelButton = createControlButton(textCancel);

        actionButton.textProperty().bind(
                Bindings.createStringBinding(() -> {
                    if (viewModel.getSimulationState().isStartable()) {
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
        bindActionButtonDisabled(actionButton);
        bindCancelButtonDisabled(cancelButton);

        actionButton.setOnAction(_ -> viewModel.requestActionButton());
        cancelButton.setOnAction(_ -> viewModel.requestCancelButton());

        HBox buttonBox = new HBox(actionButton, cancelButton);
        buttonBox.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON_HBOX);
        return buttonBox;
    }

    private Pane createControlConfigPane() {
        var simulationModeControl = FXComponentFactory
                .createLabeledChoiceRadioButtons(viewModel.simulationModeProperty(),
                        FXComponentFactory.createVBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                        AppLocalization.getText(SimulationMode.labelResourceKey()),
                        AppLocalization.getText(SimulationLocalizationKeys.CONTROL_SIMULATION_MODE_TOOLTIP),
                        FXStyleClasses.CONFIG_RADIOBUTTON
                );

        bindControlConfigDisabled(simulationModeControl.controlRegion());

        var stepDurationControl = FXComponentFactory.createLabeledIntSlider(
                viewModel.stepDurationProperty(),
                AppLocalization.getText(SimulationLocalizationKeys.CONTROL_STEP_DURATION),
                AppLocalization.getFormattedText(SimulationLocalizationKeys.CONTROL_STEP_DURATION_TOOLTIP,
                        viewModel.stepDurationProperty().min(),
                        viewModel.stepDurationProperty().max()),
                FXStyleClasses.CONFIG_SLIDER
        );

        bindControlConfigDisabled(stepDurationControl.controlRegion());

        var stepCountControl = FXComponentFactory.createLabeledIntSpinner(viewModel.stepCountProperty(),
                AppLocalization.getText(SimulationLocalizationKeys.CONTROL_STEP_COUNT),
                AppLocalization.getFormattedText(SimulationLocalizationKeys.CONTROL_STEP_COUNT_TOOLTIP,
                        viewModel.stepCountProperty().min(),
                        viewModel.stepCountProperty().max()),
                FXStyleClasses.CONFIG_SPINNER);

        bindControlConfigDisabled(stepCountControl.controlRegion());

        var startModeControl = FXComponentFactory.createLabeledChoiceCheckBox(viewModel.startModeProperty(),
                SimulationStartMode.START_PAUSED,
                SimulationStartMode.START_IMMEDIATELY,
                AppLocalization.getText(SimulationLocalizationKeys.CONTROL_START_PAUSED),
                AppLocalization.getText(SimulationLocalizationKeys.CONTROL_START_PAUSED_TOOLTIP),
                FXStyleClasses.CONFIG_CHECKBOX
        );

        bindNotStartableDisabled(startModeControl.controlRegion());

        var terminationCheckControl = FXComponentFactory
                .createLabeledChoiceCheckBox(viewModel.terminationCheckModeProperty(),
                        SimulationTerminationCheckMode.CHECKED,
                        SimulationTerminationCheckMode.UNCHECKED,
                        AppLocalization.getText(SimulationLocalizationKeys.CONTROL_TERMINATION_CHECK),
                        AppLocalization.getText(SimulationLocalizationKeys.CONTROL_TERMINATION_CHECK_TOOLTIP),
                        FXStyleClasses.CONFIG_CHECKBOX
                );

        bindControlConfigDisabled(terminationCheckControl.controlRegion());

        VBox simulationModeBox = new VBox(simulationModeControl.label(), simulationModeControl.controlRegion());
        simulationModeBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox stepDurationBox = new VBox(stepDurationControl.label(), stepDurationControl.controlRegion());
        stepDurationBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox stepCountBox = new VBox(stepCountControl.label(), stepCountControl.controlRegion());
        stepCountBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        VBox pauseBox = new VBox(startModeControl.label(), startModeControl.controlRegion(),
                terminationCheckControl.label(), terminationCheckControl.controlRegion());
        pauseBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_VBOX);

        // Show stepDurationBox only in TIMED mode
        stepDurationBox.visibleProperty()
                .bind(viewModel.simulationModeProperty().property().isEqualTo(SimulationMode.TIMED));
        stepDurationBox.managedProperty().bind(stepDurationBox.visibleProperty());

        // Show stepCountBox only in BATCH mode (not TIMED mode)
        stepCountBox.visibleProperty()
                .bind(viewModel.simulationModeProperty().property().isNotEqualTo(SimulationMode.TIMED));
        stepCountBox.managedProperty().bind(stepCountBox.visibleProperty());

        StackPane stepConfigPane = new StackPane(stepDurationBox, stepCountBox);

        HBox configBox = new HBox(simulationModeBox, stepConfigPane, pauseBox);
        configBox.getStyleClass().add(FXStyleClasses.CONTROL_CONFIG_HBOX);
        return configBox;
    }

    private Pane createControlObservationPane() {
        Label stepTitleLabel = new Label(AppLocalization.getText(SimulationLocalizationKeys.CONTROL_STEP_TITLE));

        VBox observationBox = new VBox(stepTitleLabel, stepNumberLabel);
        observationBox.getStyleClass().add(FXStyleClasses.CONTROL_OBSERVATION_VBOX);
        return observationBox;
    }

    private void bindActionButtonDisabled(Button actionButton) {
        actionButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> !viewModel.getSimulationState().isStartable()
                        && !viewModel.getSimulationState().isRunning()
                        && !viewModel.getSimulationState().isPaused(),
                viewModel.simulationStateProperty()
        ));
    }

    private void bindCancelButtonDisabled(Button cancelButton) {
        cancelButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> !viewModel.getSimulationState().isRunning()
                        && !viewModel.getSimulationState().isPaused(),
                viewModel.simulationStateProperty()
        ));
    }

    private void bindControlConfigDisabled(Region region) {
        region.disableProperty().bind(Bindings.createBooleanBinding(
                viewModel::isControlConfigDisabled,
                viewModel.simulationStateProperty()
        ));
    }

    private void bindNotStartableDisabled(Region region) {
        region.disableProperty().bind(Bindings.createBooleanBinding(
                () -> !viewModel.getSimulationState().isStartable(),
                viewModel.simulationStateProperty()
        ));
    }

}
