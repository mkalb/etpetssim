package de.mkalb.etpetssim.simulations.core.model;

/**
 * Defines whether a metric participates in minimum and/or maximum tracking.
 */
public enum StatisticExtremaMode {

    /**
     * Tracks neither minimum nor maximum values.
     */
    NONE(false, false),

    /**
     * Tracks minimum values only.
     */
    MIN(true, false),

    /**
     * Tracks maximum values only.
     */
    MAX(false, true),

    /**
     * Tracks both minimum and maximum values.
     */
    MIN_AND_MAX(true, true);

    private final boolean tracksMinimum;
    private final boolean tracksMaximum;

    StatisticExtremaMode(boolean tracksMinimum, boolean tracksMaximum) {
        this.tracksMinimum = tracksMinimum;
        this.tracksMaximum = tracksMaximum;
    }

    /**
     * Returns whether this mode tracks minimum values.
     *
     * @return {@code true} when minimum values are tracked
     */
    public boolean tracksMinimum() {
        return tracksMinimum;
    }

    /**
     * Returns whether this mode tracks maximum values.
     *
     * @return {@code true} when maximum values are tracked
     */
    public boolean tracksMaximum() {
        return tracksMaximum;
    }

}

