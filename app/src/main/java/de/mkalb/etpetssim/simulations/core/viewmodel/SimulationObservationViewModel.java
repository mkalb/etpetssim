package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtrema;
import de.mkalb.etpetssim.simulations.core.model.StatisticsSample;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.List;
import java.util.Optional;

/**
 * ViewModel contract for simulation observation data and interaction state.
 *
 * @param <STA> concrete statistics type exposed by the simulation
 */
public interface SimulationObservationViewModel<STA extends SimulationStatistics> {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

    /**
     * Returns the current statistics snapshot.
     *
     * @return the statistics snapshot, or empty before initialization or after a reset
     */
    Optional<STA> getStatistics();

    /**
     * Replaces the current statistics snapshot.
     *
     * @param statistics the new statistics snapshot
     */
    void setStatistics(STA statistics);

    /**
     * Returns the current statistic extrema snapshot.
     *
     * @return the latest extrema, or an empty snapshot before any step executes
     */
    StatisticsExtrema getStatisticsExtrema();

    /**
     * Exposes the current statistics history snapshot as an observable property.
     *
     * @return read-only property containing an immutable ordered list of samples, oldest first
     */
    ReadOnlyObjectProperty<List<StatisticsSample>> statisticsHistoryProperty();

}
