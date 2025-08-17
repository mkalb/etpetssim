package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.simulations.lab.viewmodel.LabControlViewModel;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.view.AbstractControlView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.*;

public final class LabControlView
        extends AbstractControlView<LabControlViewModel> {

    public LabControlView(LabControlViewModel viewModel) {
        super(viewModel);
    }

    @Override
    protected Region createControlButtonRegion() {
        Button drawButton = createControlButton("draw", false);
        Button drawButtonModel = createControlButton("draw model", true);
        Button drawButtonTest = createControlButton("draw test", true);

        drawButton.setOnAction(_ -> viewModel.requestDraw());
        drawButtonModel.setOnAction(_ -> viewModel.requestDrawModel());
        drawButtonTest.setOnAction(_ -> viewModel.requestDrawTest());

        drawButtonModel.disableProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.INITIAL));
        drawButtonTest.disableProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.INITIAL));

        return new HBox(drawButton, drawButtonModel, drawButtonTest);
    }

    @Override
    protected Optional<Region> createControlConfigRegion() {
        return Optional.empty();
    }

    @Override
    protected Optional<Region> createControlObservationRegion() {
        return Optional.empty();
    }

}
