package de.mkalb.etpetssim.simulations.sugar.model;

/**
 * Fixed balance values of the Sugarscape simulation that are not part of its configuration.
 *
 * <p>The configurable values and their limits are in {@link SugarConstraints}.
 */
public final class SugarBalance {

    // Sugar initialization: noise added to the linear gradient at each radius level
    /**
     * Minimum random noise offset added to the computed sugar amount during initialization.
     */
    public static final double SUGAR_NOISE_MIN = -0.4d;
    /**
     * Maximum random noise offset added to the computed sugar amount during initialization.
     */
    public static final double SUGAR_NOISE_MAX = 1.2d;

    // Sugar peak positions (fractions of grid width/height)
    /**
     * Center position fraction used to place a sugar peak at the middle of the grid.
     */
    public static final double PEAK_POSITION_CENTER = 0.5d;
    /**
     * Quarter position fraction used to place sugar peaks at one quarter of the grid dimension.
     */
    public static final double PEAK_POSITION_QUARTER = 0.25d;
    /**
     * Three-quarters position fraction used to place sugar peaks at three quarters of the grid dimension.
     */
    public static final double PEAK_POSITION_THREE_QUARTERS = 0.75d;

    private SugarBalance() {
    }

}

