package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.viewmodel.SimulationObservationViewModel;
import javafx.scene.layout.Region;

public abstract class AbstractObservationView<T extends SimulationObservationViewModel>
        implements SimulationObservationView {

    protected final T viewModel;

    protected AbstractObservationView(T viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public abstract Region buildRegion();

}