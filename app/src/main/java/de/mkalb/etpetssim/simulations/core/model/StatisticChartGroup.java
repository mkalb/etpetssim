package de.mkalb.etpetssim.simulations.core.model;

/**
 * Controls whether a metric is charted and in which sub-chart group it appears.
 * Declaration order defines the stacking order in the observation area ({@code PRIMARY} on top).
 */
public enum StatisticChartGroup {

    /**
     * Metric is not charted.
     */
    NONE,

    /**
     * Charted in the primary (top) sub-chart.
     */
    PRIMARY,

    /**
     * Charted in the secondary (bottom) sub-chart.
     */
    SECONDARY

}
