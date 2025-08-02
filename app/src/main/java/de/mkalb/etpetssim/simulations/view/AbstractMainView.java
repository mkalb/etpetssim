package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.viewmodel.BaseMainViewModel;
import javafx.scene.layout.Region;

public abstract class AbstractMainView<T extends BaseMainViewModel> implements BaseMainView {

    protected final T viewModel;

    protected AbstractMainView(T viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public final Region buildViewRegion() {
        return buildRegion();
    }

    @Override
    public abstract Region buildRegion();

}