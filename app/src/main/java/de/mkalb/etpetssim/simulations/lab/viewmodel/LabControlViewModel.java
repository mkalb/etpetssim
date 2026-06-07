package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationControlViewModel;
import javafx.beans.property.*;

public final class LabControlViewModel
        implements SimulationControlViewModel {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final BooleanProperty drawRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty drawModelRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty drawTestRequested = new SimpleBooleanProperty(false);

    public LabControlViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    @Override
    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    public BooleanProperty drawRequestedProperty() {
        return drawRequested;
    }

    public BooleanProperty drawModelRequestedProperty() {
        return drawModelRequested;
    }

    public BooleanProperty drawTestRequestedProperty() {
        return drawTestRequested;
    }

    public void requestDraw() {
        drawRequested.set(true);
    }

    public void requestDrawModel() {
        drawModelRequested.set(true);
    }

    public void requestDrawTest() {
        drawTestRequested.set(true);
    }

}
