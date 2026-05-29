package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Available start modes for simulation initialization.
 *
 * <p>{@code START_IMMEDIATELY} begins execution right after initialization. {@code START_PAUSED}
 * initializes the simulation but leaves it paused so the user may inspect or modify state
 * before starting.
 *
 * @see SimulationState
 */
public enum SimulationStartMode {

    /** Start running immediately after initialization. */
    START_IMMEDIATELY,

    /** Initialize in a paused state so manual intervention is possible before running. */
    START_PAUSED

}
