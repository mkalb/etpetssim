package de.mkalb.etpetssim.wator;

public final class WaTorInteractor {

    private final WaTorModel waTorModel;
    private final WaTorBroker waTorBroker;

    public WaTorInteractor(WaTorModel waTorModel) {
        this.waTorModel = waTorModel;
        waTorBroker = new WaTorBroker();
    }

    public boolean startSimulation() {
        waTorModel.resetTimeCounter();
        System.out.println("Start simulation: " + waTorModel.timeCounter());
        return false;
    }

    public boolean updateSimulation() {
        waTorModel.incrementTimeCounter();
        System.out.println("Update simulation: " + waTorModel.timeCounter());
        return waTorModel.timeCounter() > 50;
    }

}
