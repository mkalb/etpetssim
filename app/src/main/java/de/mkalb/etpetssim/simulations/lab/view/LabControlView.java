package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.AbstractControlView;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabControlViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.*;

public final class LabControlView
        extends AbstractControlView<LabControlViewModel> {

    private static final String LAB_CONTROL_DRAW = "lab.control.draw";
    private static final String LAB_CONTROL_DRAW_MODEL = "lab.control.drawmodel";
    private static final String LAB_CONTROL_DRAW_TEST = "lab.control.drawtest";

    public LabControlView(LabControlViewModel viewModel) {
        super(viewModel);
    }

    @Override
    protected Pane createControlButtonPane() {
        Button drawButton = createControlButton(AppLocalization.getText(LAB_CONTROL_DRAW), false);
        Button drawButtonModel = createControlButton(AppLocalization.getText(LAB_CONTROL_DRAW_MODEL), true);
        Button drawButtonTest = createControlButton(AppLocalization.getText(LAB_CONTROL_DRAW_TEST), true);

        drawButton.setOnAction(_ -> viewModel.requestDraw());
        drawButtonModel.setOnAction(_ -> viewModel.requestDrawModel());
        drawButtonTest.setOnAction(_ -> viewModel.requestDrawTest());

        drawButtonModel.disableProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.INITIAL));
        drawButtonTest.disableProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.INITIAL));

        HBox buttonBox = new HBox(drawButton, drawButtonModel, drawButtonTest);
        buttonBox.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON_HBOX);
        return buttonBox;
    }

    @Override
    protected Optional<Pane> createControlConfigPane() {
        return Optional.empty();
    }

    @Override
    protected Optional<Pane> createControlObservationPane() {
        return Optional.empty();
    }

    @Override
    public void updateStepCount(int stepCount) {
        // Do nothing
    }

}
