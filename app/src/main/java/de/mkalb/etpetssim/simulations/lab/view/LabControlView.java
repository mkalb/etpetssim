package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabControlViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractControlView;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;

public final class LabControlView extends AbstractControlView<LabControlViewModel> {

    public LabControlView(LabControlViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildRegion() {
        Button drawButton = buildControlButton("draw", false);
        Button drawButtonModel = buildControlButton("draw model", true);
        Button drawButtonTest = buildControlButton("draw test", true);

        drawButton.setOnAction(_ -> viewModel.requestDraw());
        drawButtonModel.setOnAction(_ -> viewModel.requestDrawModel());
        drawButtonTest.setOnAction(_ -> viewModel.requestDrawTest());

        drawButtonModel.disableProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.READY));
        drawButtonTest.disableProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.READY));

        return createControlMainBox(drawButton, drawButtonModel, drawButtonTest);
    }

}
