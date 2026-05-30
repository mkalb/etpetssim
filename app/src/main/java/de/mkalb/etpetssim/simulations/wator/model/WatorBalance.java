package de.mkalb.etpetssim.simulations.wator.model;

/**
 * Simulation balance constants for the Wa-Tor predator-prey simulation.
 * <p>
 * Contains tunable parameters that govern simulation dynamics but are not
 * exposed as user-configurable settings.
 */
public final class WatorBalance {

    // Termination
    /**
     * Fish population share above which the simulation ends when all sharks are gone.
     * The simulation terminates when sharks are extinct and fish exceed this fraction of all cells.
     */
    public static final double TERMINATION_FISH_MAX_SHARE = 0.9d;

    /**
     * Private constructor to prevent instantiation.
     */
    private WatorBalance() {
    }

}

