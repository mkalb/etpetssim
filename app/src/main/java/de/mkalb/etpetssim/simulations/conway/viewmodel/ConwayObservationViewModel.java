package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public final class ConwayObservationViewModel {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final ReadOnlyObjectWrapper<ConwayStatistics> statistics = new ReadOnlyObjectWrapper<>();

    public ConwayObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    public ReadOnlyObjectProperty<ConwayStatistics> statisticsProperty() {
        return statistics.getReadOnlyProperty();
    }

    public ConwayStatistics getStatistics() {
        return statistics.get();
    }

    // Called by parent ViewModel when statistics are updated
    public void setStatistics(ConwayStatistics stats) {
        statistics.set(stats);
    }

}