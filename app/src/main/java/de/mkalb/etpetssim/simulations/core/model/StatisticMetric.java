package de.mkalb.etpetssim.simulations.core.model;

import java.util.function.*;

/**
 * Describes one numeric metric that can be sampled from simulation statistics.
 *
 * @param <STA>           simulation statistics type
 * @param key             technical metric key
 * @param labelKey        localization key used for display labels
 * @param extractor       function extracting the metric from live statistics
 * @param extremaMode     min/max tracking policy
 * @param chartGroup      chart group assignment; {@link StatisticChartGroup#NONE} means not charted
 * @param chartWindowSize number of trailing history samples rendered on the chart for this metric;
 *                        must be {@code 0} when {@code chartGroup} is {@link StatisticChartGroup#NONE},
 *                        and {@code > 0} otherwise; metrics sharing the same {@code chartGroup} must
 *                        use the same value
 */
public record StatisticMetric<STA extends SimulationStatistics>(
        String key,
        String labelKey,
        ToDoubleFunction<STA> extractor,
        StatisticExtremaMode extremaMode,
        StatisticChartGroup chartGroup,
        int chartWindowSize) {

    /**
     * Default number of trailing history samples rendered on a chart, close to the previous
     * (pre-history-capacity-increase) {@code StatisticHistory} capacity.
     */
    public static final int DEFAULT_CHART_WINDOW_SIZE = 100;

    public StatisticMetric {
        if (key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        if (labelKey.isBlank()) {
            throw new IllegalArgumentException("labelKey must not be blank");
        }
        if (chartGroup == StatisticChartGroup.NONE) {
            if (chartWindowSize != 0) {
                throw new IllegalArgumentException("chartWindowSize must be 0 when chartGroup is NONE");
            }
        } else if (chartWindowSize <= 0) {
            throw new IllegalArgumentException("chartWindowSize must be > 0 when chartGroup is not NONE");
        }
    }

    /**
     * Convenience constructor for metrics that are not charted ({@code chartGroup = NONE}).
     *
     * @param key         technical metric key
     * @param labelKey    localization key used for display labels
     * @param extractor   function extracting the metric from live statistics
     * @param extremaMode min/max tracking policy
     */
    public StatisticMetric(String key, String labelKey, ToDoubleFunction<STA> extractor, StatisticExtremaMode extremaMode) {
        this(key, labelKey, extractor, extremaMode, StatisticChartGroup.NONE, 0);
    }

    /**
     * Convenience constructor using {@link #DEFAULT_CHART_WINDOW_SIZE} for charted metrics and {@code 0} for
     * uncharted metrics.
     *
     * @param key         technical metric key
     * @param labelKey    localization key used for display labels
     * @param extractor   function extracting the metric from live statistics
     * @param extremaMode min/max tracking policy
     * @param chartGroup  chart group assignment
     */
    public StatisticMetric(String key, String labelKey, ToDoubleFunction<STA> extractor, StatisticExtremaMode extremaMode,
                           StatisticChartGroup chartGroup) {
        this(key, labelKey, extractor, extremaMode, chartGroup,
                (chartGroup == StatisticChartGroup.NONE) ? 0 : DEFAULT_CHART_WINDOW_SIZE);
    }

}

