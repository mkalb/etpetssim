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
 */
public record StatisticMetric<STA extends SimulationStatistics>(
        String key,
        String labelKey,
        ToDoubleFunction<STA> extractor,
        StatisticExtremaMode extremaMode) {

    public StatisticMetric {
        if (key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        if (labelKey.isBlank()) {
            throw new IllegalArgumentException("labelKey must not be blank");
        }
    }

}

