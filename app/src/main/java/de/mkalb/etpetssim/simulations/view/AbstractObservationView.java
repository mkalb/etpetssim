package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.viewmodel.SimulationObservationViewModel;
import javafx.scene.layout.Region;

public abstract class AbstractObservationView<VM extends SimulationObservationViewModel>
        implements SimulationObservationView {

    protected final VM viewModel;

    protected AbstractObservationView(VM viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public abstract Region buildObservationRegion();

}