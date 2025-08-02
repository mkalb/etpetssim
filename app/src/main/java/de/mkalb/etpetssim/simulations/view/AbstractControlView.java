package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.viewmodel.BaseControlViewModel;
import javafx.scene.layout.Region;

public abstract class AbstractControlView<T extends BaseControlViewModel> implements BaseControlView {

    protected final T viewModel;

    protected AbstractControlView(T viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public abstract Region buildRegion();

}