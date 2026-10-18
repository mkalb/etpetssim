package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * ViewModel contract for simulation observation data and interaction state.
 *
 * @param <STA> concrete statistics type exposed by the simulation
 */
public interface SimulationObservationViewModel<STA extends SimulationStatistics> {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

    /**
     * Exposes the current statistics snapshot.
     *
     * <p>The value is {@code null} before initialization or after a reset.
     *
     * @return read-only property containing the current statistics or {@code null}
     */
    ReadOnlyObjectProperty<@Nullable STA> statisticsProperty();

    /**
     * Returns the current statistics snapshot.
     *
     * @return the statistics snapshot, or empty before initialization or after a reset
     */
    Optional<STA> getStatistics();

    /**
     * Replaces the current statistics snapshot.
     *
     * @param statistics the new statistics snapshot, never {@code null}
     */
    void setStatistics(STA statistics);

    /**
     * Exposes the last clicked grid coordinate.
     *
     * @return mutable property that may contain {@code null} when no cell is selected
     */
    ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty();

    /**
     * Exposes the current statistic extrema snapshot as an observable property.
     *
     * @return read-only property containing the latest extrema
     */
    ReadOnlyObjectProperty<StatisticsExtrema> statisticsExtremaProperty();

    /**
     * Exposes the current statistics history snapshot as an observable property.
     *
     * @return read-only property containing an immutable ordered list of samples, oldest first
     */
    ReadOnlyObjectProperty<List<StatisticsSample>> statisticsHistoryProperty();

}
