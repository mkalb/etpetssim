package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Available start modes for simulation initialization.
 *
 * @see SimulationState
 */
public enum SimulationStartMode {

    /**
     * Start running immediately after initialization.
     */
    START_IMMEDIATELY,

    /**
     * Initialize in a paused state so manual intervention is possible before running.
     */
    START_PAUSED

}
