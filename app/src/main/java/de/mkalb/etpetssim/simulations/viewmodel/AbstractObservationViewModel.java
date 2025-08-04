package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class AbstractObservationViewModel<S> implements SimulationObservationViewModel<S> {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final ReadOnlyObjectWrapper<S> statistics;

    protected AbstractObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
        statistics = new ReadOnlyObjectWrapper<>();
    }

    @Override
    public final ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public final SimulationState getSimulationState() {
        return simulationState.get();
    }

    @Override
    public final ReadOnlyObjectProperty<S> statisticsProperty() {
        return statistics.getReadOnlyProperty();
    }

    @Override
    public final S getStatistics() {
        return statistics.get();
    }

    @Override
    public final void setStatistics(S stats) {
        statistics.set(stats);
    }

}