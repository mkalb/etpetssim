package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.viewmodel.BaseConfigViewModel;
import javafx.scene.layout.Region;

public abstract class AbstractConfigView<T extends BaseConfigViewModel> implements BaseConfigView {

    protected final T viewModel;

    protected AbstractConfigView(T viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public abstract Region buildRegion();

}