package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.model.SimulationStatistics;
import javafx.beans.property.*;

public abstract class AbstractMainViewModel<CON extends SimulationConfig, STA extends SimulationStatistics>
        implements SimulationMainViewModel {

    protected final SimulationConfigViewModel<CON> configViewModel;
    protected final SimulationObservationViewModel<STA> observationViewModel;
    private final ObjectProperty<SimulationState> simulationState;
    private final BooleanProperty simulationTimeoutProperty = new SimpleBooleanProperty(false);

    protected AbstractMainViewModel(ObjectProperty<SimulationState> simulationState,
                                    SimulationConfigViewModel<CON> configViewModel,
                                    SimulationObservationViewModel<STA> observationViewModel) {
        this.simulationState = simulationState;
        this.configViewModel = configViewModel;
        this.observationViewModel = observationViewModel;
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

    protected abstract CON getCurrentConfig();

    public abstract boolean hasSimulationManager();

}
