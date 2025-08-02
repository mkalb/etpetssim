package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

public abstract class AbstractControlViewModel implements BaseControlViewModel {

    protected final ReadOnlyObjectProperty<SimulationState> simulationState;

    protected AbstractControlViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
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

}