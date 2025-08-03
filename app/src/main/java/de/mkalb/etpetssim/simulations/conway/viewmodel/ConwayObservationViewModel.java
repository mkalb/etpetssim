package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractObservationViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public final class ConwayObservationViewModel extends AbstractObservationViewModel {

    private final ReadOnlyObjectWrapper<ConwayStatistics> statistics = new ReadOnlyObjectWrapper<>();

    public ConwayObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
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