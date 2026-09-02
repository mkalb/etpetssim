package de.mkalb.etpetssim.engine.support;

/**
 * Shared constants for bounded checks in long-running loops.
 */
public final class WorkCheckpoints {

    /**
     * Number of loop iterations between cooperative cancellation checks.
     */
    public static final int CANCELLATION_CHECK_INTERVAL = 1_024;

    /**
     * Bit mask for testing whether a zero-based loop index reaches a cancellation checkpoint.
     */
    public static final int CANCELLATION_CHECK_MASK = CANCELLATION_CHECK_INTERVAL - 1;

    /**
     * Private constructor to prevent instantiation.
     */
    private WorkCheckpoints() {
    }

}
