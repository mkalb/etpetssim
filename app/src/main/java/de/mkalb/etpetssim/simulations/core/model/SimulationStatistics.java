package de.mkalb.etpetssim.simulations.core.model;

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
     * Returns the total number of cells in the simulation grid.
     *
     * @return total cell count
     */
    int getTotalCells();

}
