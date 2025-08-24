package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.model.SimulationStatistics;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.*;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class AbstractObservationViewModel<STA extends SimulationStatistics>
        implements SimulationObservationViewModel<STA> {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final ReadOnlyObjectWrapper<STA> statistics;

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
    public final ReadOnlyObjectProperty<STA> statisticsProperty() {
        return statistics.getReadOnlyProperty();
    }

    @SuppressWarnings("OptionalOfNullableMisuse")
    @Override
    public final Optional<STA> getStatistics() {
        return Optional.ofNullable(statistics.get());
    }

    @Override
    public final void setStatistics(STA stats) {
        statistics.set(stats);
    }

}
