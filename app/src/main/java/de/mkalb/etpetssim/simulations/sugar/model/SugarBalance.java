package de.mkalb.etpetssim.simulations.sugar.model;

/**
 * Non-configurable balance constants for the Sugarscape simulation.
 * <p>
 * Contains algorithm parameters that are fixed by the simulation design and are not exposed
 * as user-configurable settings. Complements {@link SugarConstraints} which holds
 * UI-facing constraint min/max/default values.
 */
public final class SugarBalance {

    // Sugar initialization – noise added to the linear gradient at each radius level
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

    /**
     * Private constructor to prevent instantiation.
     */
    private SugarBalance() {
    }

}

