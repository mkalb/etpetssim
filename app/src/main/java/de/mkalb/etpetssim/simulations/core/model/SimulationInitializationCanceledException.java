package de.mkalb.etpetssim.simulations.core.model;

/**
 * Signals that cooperative simulation initialization was canceled.
 */
public final class SimulationInitializationCanceledException
        extends RuntimeException {

    /**
     * Creates an exception without a stack trace because cancellation is an expected lifecycle event.
     */
    SimulationInitializationCanceledException() {
        super("Simulation initialization was canceled.", null, false, false);
    }

}
