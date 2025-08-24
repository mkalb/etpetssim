package de.mkalb.etpetssim.simulations.model;

/**
 * Defines the available start modes for initializing a simulation.
 * <p>
 * Each mode determines whether the simulation begins execution immediately
 * or is initialized in a paused state, allowing for manual intervention before running.
 * <p>
 * The start mode is selected by the user in the control panel and affects the initial
 * {@link SimulationState} after simulation initialization.
 *
 * @see SimulationState
 * @see de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel
 */
public enum SimulationStartMode {

    /**
     * The simulation starts running immediately after initialization.
     * <p>
     * No manual intervention is possible before the first step.
     */
    RUNNING,

    /**
     * The simulation is initialized in a paused state.
     * <p>
     * Manual editing and intervention are possible before starting execution.
     */
    PAUSED

}
