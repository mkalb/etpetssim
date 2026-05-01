package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * ViewModel contract for simulation observation data and interaction state.
 *
 * @param <STA> concrete statistics type exposed by the simulation
 */
public interface SimulationObservationViewModel<STA extends SimulationStatistics> {

    /**
     * Exposes the current simulation state as read-only JavaFX property.
     *
     * @return read-only simulation state property
     */
    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    /**
     * Returns the current simulation state.
     *
     * @return current simulation state
     */
    SimulationState getSimulationState();

    /**
     * Exposes the current statistics snapshot.
     * <p>
     * The value is nullable before first initialization.
     *
     * @return read-only property containing the current statistics or {@code null}
     */
    ReadOnlyObjectProperty<@Nullable STA> statisticsProperty();

    /**
     * Returns the current statistics snapshot as optional value.
     *
     * @return optional statistics snapshot
     */
    Optional<STA> getStatistics();

    /**
     * Updates the statistics snapshot shown by the view model.
     *
     * @param stats non-null statistics snapshot
     */
    void setStatistics(STA stats);

    /**
     * Exposes the last clicked grid coordinate.
     *
     * @return mutable property that may contain {@code null} when no cell is selected
     */
    ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty();

}
