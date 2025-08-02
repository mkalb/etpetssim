package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.viewmodel.BaseObservationViewModel;
import javafx.scene.layout.Region;

public abstract class AbstractObservationView<T extends BaseObservationViewModel> implements BaseObservationView {

    protected final T viewModel;

    protected AbstractObservationView(T viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public abstract Region buildRegion();

}