package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class AbstractControlViewModel implements SimulationControlViewModel {

    protected final ReadOnlyObjectProperty<SimulationState> simulationState;

    protected AbstractControlViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
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

}