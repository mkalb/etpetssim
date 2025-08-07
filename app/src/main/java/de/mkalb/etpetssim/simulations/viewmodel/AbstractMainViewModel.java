package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import javafx.beans.property.*;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class AbstractMainViewModel
        implements SimulationMainViewModel {

    private final ObjectProperty<SimulationState> simulationState;
    private final BooleanProperty simulationTimeoutProperty = new SimpleBooleanProperty(false);

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

    protected final void setSimulationState(SimulationState state) {
        simulationState.set(state);
    }

    @Override
    public BooleanProperty simulationTimeoutProperty() {
        return simulationTimeoutProperty;
    }

    protected final void setSimulationTimeout(boolean timeout) {
        simulationTimeoutProperty.set(timeout);
    }

}
