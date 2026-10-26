package de.mkalb.etpetssim.simulations.wator.model;

/**
 * Fixed balance values of the Wa-Tor simulation that are not part of its configuration.
 *
 * <p>The configurable values and their limits are in {@link WatorConstraints}.
 */
public final class WatorBalance {

    // Termination
    /**
     * Fish population share above which the simulation ends when all sharks are gone.
     * The simulation terminates when sharks are extinct and fish exceed this fraction of all cells.
     */
    public static final double TERMINATION_FISH_MAX_SHARE = 0.9d;

    private WatorBalance() {
    }

}

