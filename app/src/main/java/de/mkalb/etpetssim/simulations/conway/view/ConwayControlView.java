package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayControlViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractControlView;
import de.mkalb.etpetssim.ui.FXComponentBuilder;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class ConwayControlView extends AbstractControlView<ConwayControlViewModel> {

    public ConwayControlView(ConwayControlViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildRegion() {
        // Button
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

        actionButton.setOnAction(_ -> viewModel.requestActionButton());
        cancelButton.setOnAction(_ -> viewModel.requestCancelButton());

        // Config
        var stepDurationControl = FXComponentBuilder.createLabeledIntSlider(
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