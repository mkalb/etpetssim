package de.mkalb.etpetssim.simulations.model;

/**
 * Defines whether the simulation termination condition should be checked during execution.
 * <p>
 * This enum allows configuration of whether the simulation should actively check
 * its termination condition or skip this check.
 * <p>
 * Use {@code CHECKED} to enable termination checking, or {@code UNCHECKED} to disable it.
 */
public enum SimulationTerminationCheck {

    /**
     * The termination condition is checked during simulation execution.
     */
    CHECKED,

    /**
     * The termination condition is not checked during simulation execution.
     */
    UNCHECKED

}
