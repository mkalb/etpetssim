package de.mkalb.etpetssim.wator;

import javafx.scene.layout.Region;

public final class WaTorController {

    private final WaTorViewBuilder waTorViewBuilder;
    private final WaTorInteractor waTorInteractor;

    public WaTorController() {
        WaTorModel waTorModel = new WaTorModel();
        waTorInteractor = new WaTorInteractor(waTorModel);
        waTorViewBuilder = new WaTorViewBuilder(waTorModel,
                this::startSimulation,
                this::updateSimulation);
    }

    public Region buildViewRegion() {
        return waTorViewBuilder.build();
    }

    public boolean startSimulation() {
        return waTorInteractor.startSimulation();
    }

    public boolean updateSimulation() {
        return waTorInteractor.updateSimulation();
    }

}
