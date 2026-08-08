package de.mkalb.etpetssim.simulations.core.model;

import java.util.*;

/**
 * Mutable accumulator that updates min/max values according to configured metric policies.
 */
final class StatisticExtremaTracker {

    private final Map<String, StatisticExtremaMode> extremaModesByKey;
    private final LinkedHashMap<String, Double> minimumValues;
    private final LinkedHashMap<String, Double> maximumValues;

    StatisticExtremaTracker(List<? extends StatisticMetric<?>> metrics) {
        extremaModesByKey = new LinkedHashMap<>();
        for (var metric : metrics) {
            if (extremaModesByKey.put(metric.key(), metric.extremaMode()) != null) {
                throw new IllegalArgumentException("duplicate metric key: " + metric.key());
            }
        }
        minimumValues = new LinkedHashMap<>();
        maximumValues = new LinkedHashMap<>();
    }

    synchronized void clear() {
        minimumValues.clear();
        maximumValues.clear();
    }

    synchronized void update(Map<String, Double> values) {
        for (var entry : values.entrySet()) {
            String key = entry.getKey();
            StatisticExtremaMode mode = extremaModesByKey.get(key);
            if (mode == null) {
                continue;
            }
            double value = entry.getValue();
            if (!Double.isFinite(value)) {
                continue;
            }
            if ((mode == StatisticExtremaMode.MIN) || (mode == StatisticExtremaMode.MIN_AND_MAX)) {
                minimumValues.merge(key, value, Math::min);
            }
            if ((mode == StatisticExtremaMode.MAX) || (mode == StatisticExtremaMode.MIN_AND_MAX)) {
                maximumValues.merge(key, value, Math::max);
            }
        }
    }

    synchronized StatisticExtrema snapshot() {
        return new StatisticExtrema(minimumValues, maximumValues);
    }

}

