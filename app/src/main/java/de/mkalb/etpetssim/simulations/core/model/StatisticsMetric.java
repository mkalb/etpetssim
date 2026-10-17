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
 * @param chartGroup      chart group assignment; {@link StatisticsChartGroup#NONE} means not charted
 * @param chartWindowSize number of trailing history samples rendered on the chart for this metric;
 *                        must be {@code 0} when {@code chartGroup} is {@link StatisticsChartGroup#NONE},
 *                        and {@code > 0} otherwise; metrics sharing the same {@code chartGroup} must
 *                        use the same value
 */
public record StatisticsMetric<STA extends SimulationStatistics>(
        String key,
        String labelKey,
        ToDoubleFunction<STA> extractor,
        StatisticsExtremaMode extremaMode,
        StatisticsChartGroup chartGroup,
        int chartWindowSize) {

    /**
     * Default number of trailing history samples rendered on a chart.
     */
    public static final int DEFAULT_CHART_WINDOW_SIZE = 100;

    public StatisticsMetric {
        if (key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        if (labelKey.isBlank()) {
            throw new IllegalArgumentException("labelKey must not be blank");
        }
        if (chartGroup == StatisticsChartGroup.NONE) {
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
    public StatisticsMetric(String key, String labelKey, ToDoubleFunction<STA> extractor, StatisticsExtremaMode extremaMode) {
        this(key, labelKey, extractor, extremaMode, StatisticsChartGroup.NONE, 0);
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
    public StatisticsMetric(String key, String labelKey, ToDoubleFunction<STA> extractor, StatisticsExtremaMode extremaMode,
                            StatisticsChartGroup chartGroup) {
        this(key, labelKey, extractor, extremaMode, chartGroup,
                (chartGroup == StatisticsChartGroup.NONE) ? 0 : DEFAULT_CHART_WINDOW_SIZE);
    }

}

