package de.mkalb.etpetssim.simulations.wator.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public final class WatorObservationViewModel extends AbstractObservationViewModel {

    private final ReadOnlyObjectWrapper<WatorStatistics> statistics = new ReadOnlyObjectWrapper<>();

    public WatorObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
    }

    public ReadOnlyObjectProperty<WatorStatistics> statisticsProperty() {
        return statistics.getReadOnlyProperty();
    }

    public WatorStatistics getStatistics() {
        return statistics.get();
    }

    // Called by parent ViewModel when statistics are updated
    public void setStatistics(WatorStatistics stats) {
        statistics.set(stats);
    }

}