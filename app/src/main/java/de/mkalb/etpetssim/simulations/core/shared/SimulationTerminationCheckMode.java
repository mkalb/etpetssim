package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Controls whether the simulation termination condition is evaluated during execution.
 *
 * <p>Use {@code CHECKED} to pause the simulation when its termination condition is met after a step. Use
 * {@code UNCHECKED} to let it continue beyond that point.
 */
public enum SimulationTerminationCheckMode {

    /**
     * Termination condition is evaluated during simulation execution.
     */
    CHECKED,

    /**
     * Termination condition is not evaluated during simulation execution.
     */
    UNCHECKED

}
