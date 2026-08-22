package de.mkalb.etpetssim.simulations.core.model;

import java.util.*;

/**
 * Mutable accumulator that updates min/max extrema according to configured metric policies.
 */
final class StatisticExtremaTracker {

    private final Map<String, StatisticExtremaMode> extremaModesByKey;
    private final LinkedHashMap<String, StatisticExtremum> minimumValues;
    private final LinkedHashMap<String, StatisticExtremum> maximumValues;

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

    synchronized void update(Map<String, Double> values, long stepCount) {
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
            var candidate = new StatisticExtremum(value, stepCount);
            if (mode.tracksMinimum()) {
                minimumValues.merge(key, candidate,
                        (existing, incoming) -> (incoming.value() < existing.value()) ? incoming : existing);
            }
            if (mode.tracksMaximum()) {
                maximumValues.merge(key, candidate,
                        (existing, incoming) -> (incoming.value() > existing.value()) ? incoming : existing);
            }
        }
    }

    synchronized StatisticExtrema snapshot() {
        return new StatisticExtrema(minimumValues, maximumValues);
    }

}

