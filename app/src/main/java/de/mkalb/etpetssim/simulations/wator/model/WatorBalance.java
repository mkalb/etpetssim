package de.mkalb.etpetssim.simulations.wator.model;

/**
 * Fixed balance values of the Wa-Tor simulation that are not part of its configuration.
 *
 * <p>The configurable values and their limits are in {@link WatorConstraints}.
 */
public final class WatorBalance {

    // Termination
    /**
     * Share of all cells above which the fish end the simulation once all sharks are extinct.
     */
    public static final double TERMINATION_FISH_MAX_SHARE = 0.9d;

    private WatorBalance() {
    }

}
