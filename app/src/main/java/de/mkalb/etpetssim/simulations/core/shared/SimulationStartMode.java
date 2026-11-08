package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Available start modes for simulation initialization.
 */
public enum SimulationStartMode {

    /**
     * Starts running immediately after initialization.
     */
    START_IMMEDIATELY,

    /**
     * Starts in the state {@code PAUSED} after initialization, so that the user can edit the grid before running.
     */
    START_PAUSED

}
