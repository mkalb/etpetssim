package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;

/**
 * Provides shared counters that describe a simulation snapshot.
 */
public interface SimulationStatistics {

    /**
     * Returns the current simulation step count.
     *
     * @return current step count
     */
    int getStepCount();

    /**
     * Returns the grid structure of the simulation snapshot.
     *
     * @return grid structure used by the simulation
     */
    GridStructure getGridStructure();

    /**
     * Returns the total number of cells in the simulation grid.
     * The default implementation derives the value from {@link #getGridStructure()}.
     *
     * @return total cell count
     */
    default int getTotalCells() {
        return getGridStructure().cellCount();
    }

}
