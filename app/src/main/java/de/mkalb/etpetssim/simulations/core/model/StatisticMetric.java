package de.mkalb.etpetssim.simulations.core.model;

import java.util.function.*;

/**
 * Describes one numeric metric that can be sampled from simulation statistics.
 *
 * @param <STA>       simulation statistics type
 * @param key         technical metric key
 * @param labelKey    localization key used for display labels
 * @param extractor   function extracting the metric from live statistics
 * @param extremaMode min/max tracking policy
 * @param chartGroup  chart group assignment; {@link StatisticChartGroup#NONE} means not charted
 */
public record StatisticMetric<STA extends SimulationStatistics>(
        String key,
        String labelKey,
        ToDoubleFunction<STA> extractor,
        StatisticExtremaMode extremaMode,
        StatisticChartGroup chartGroup) {

    public StatisticMetric {
        if (key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        if (labelKey.isBlank()) {
            throw new IllegalArgumentException("labelKey must not be blank");
        }
    }

    /**
     * Convenience constructor for metrics that are not charted ({@code chartGroup = NONE}).
     */
    public StatisticMetric(String key, String labelKey, ToDoubleFunction<STA> extractor, StatisticExtremaMode extremaMode) {
        this(key, labelKey, extractor, extremaMode, StatisticChartGroup.NONE);
    }

}

