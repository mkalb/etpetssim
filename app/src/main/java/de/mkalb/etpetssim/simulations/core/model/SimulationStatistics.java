package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;

/**
 * Provides shared counters and the step timing that describe a simulation snapshot.
 */
public interface SimulationStatistics {

    /**
     * Returns the current simulation step count.
     *
     * @return current step count
     */
    int stepCount();

    /**
     * Returns the grid structure of the simulation snapshot.
     *
     * @return grid structure used by the simulation
     */
    GridStructure gridStructure();

    /**
     * Returns timing statistics for simulation steps.
     *
     * @return timing statistics snapshot
     */
    StepTimingStatistics stepTimingStatistics();

    /**
     * Returns the total number of cells in the simulation grid.
     * The default implementation derives the value from {@link #gridStructure()}.
     *
     * @return total cell count
     */
    default int totalCells() {
        return gridStructure().cellCount();
    }

}
