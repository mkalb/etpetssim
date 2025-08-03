package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public abstract class AbstractMainViewModel implements BaseMainViewModel {

    private final ObjectProperty<SimulationState> simulationState;

    protected AbstractMainViewModel(ObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    @Override
    public final ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public final SimulationState getSimulationState() {
        return simulationState.get();
    }

    protected void setSimulationState(SimulationState state) {
        simulationState.set(state);
    }

}