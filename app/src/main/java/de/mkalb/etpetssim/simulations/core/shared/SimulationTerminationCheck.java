package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Controls whether the simulation termination condition is evaluated during execution.
 *
 * <p>Use {@code CHECKED} when the simulation should evaluate its termination condition
 * every step (or at configured intervals). Use {@code UNCHECKED} to skip termination
 * checks for runs where termination is managed externally or not required.
 */
public enum SimulationTerminationCheck {

    /**
     * Termination condition is evaluated during simulation execution.
     */
    CHECKED,

    /**
     * Termination condition is not evaluated during simulation execution.
     */
    UNCHECKED

}
