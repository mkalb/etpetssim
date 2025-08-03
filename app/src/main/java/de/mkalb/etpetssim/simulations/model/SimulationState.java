package de.mkalb.etpetssim.simulations.model;

/**
 * Defines the possible states of a simulation's lifecycle.
 * Used to represent and control the current execution state of a simulation.
 *
 * <ul>
 *   <li>{@link #READY} &ndash; No simulation instance exists yet. A new simulation can be created, initialized, and started.</li>
 *   <li>{@link #RUNNING} &ndash; The simulation is currently running.</li>
 *   <li>{@link #PAUSED} &ndash; The simulation is temporarily paused and can be resumed.</li>
 * </ul>
 */
public enum SimulationState {

    /**
     * No simulation instance exists yet.
     * A new simulation can be created, initialized, and started.
     */
    READY,

    /**
     * The simulation is currently running.
     */
    RUNNING,

    /**
     * The simulation is temporarily paused and can be resumed.
     */
    PAUSED

}
