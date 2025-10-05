package de.mkalb.etpetssim.simulations.lab.view;

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

    public LabControlView(LabControlViewModel viewModel) {
        super(viewModel);
    }

    @Override
    protected Pane createControlButtonPane() {
        Button drawButton = createControlButton("Draw", false);
        Button drawButtonModel = createControlButton("Draw Model", true);
        Button drawButtonTest = createControlButton("Draw Test", true);

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

}
