// In ConwayObservationViewModel.java
package de.mkalb.etpetssim.simulations.conwayslife.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwayStatistics;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public final class ConwayObservationViewModel {

    private final ObjectProperty<SimulationState> simulationState;
    private final ReadOnlyObjectWrapper<ConwayStatistics> statistics = new ReadOnlyObjectWrapper<>();

    public ConwayObservationViewModel(ObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    public void setSimulationState(SimulationState state) {
        simulationState.set(state);
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