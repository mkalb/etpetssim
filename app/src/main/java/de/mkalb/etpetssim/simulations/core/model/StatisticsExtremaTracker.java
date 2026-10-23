package de.mkalb.etpetssim.simulations.core.model;

import java.util.*;

/**
 * Mutable accumulator that updates min/max extrema according to configured metric policies.
 */
final class StatisticsExtremaTracker {

    private final Map<String, StatisticsExtremaMode> extremaModesByKey;
    private final LinkedHashMap<String, StatisticsExtremum> minimumValues;
    private final LinkedHashMap<String, StatisticsExtremum> maximumValues;

    StatisticsExtremaTracker(List<? extends StatisticsMetric<?>> metrics) {
        extremaModesByKey = new LinkedHashMap<>();
        for (var metric : metrics) {
            if (extremaModesByKey.put(metric.key(), metric.extremaMode()) != null) {
                throw new IllegalArgumentException("duplicate metric key: " + metric.key());
            }
        }
        minimumValues = new LinkedHashMap<>();
        maximumValues = new LinkedHashMap<>();
    }

    synchronized void recordSample(Map<String, Double> values, int stepCount) {
        for (var entry : values.entrySet()) {
            String key = entry.getKey();
            StatisticsExtremaMode mode = extremaModesByKey.get(key);
            if (mode == null) {
                continue;
            }
            double value = entry.getValue();
            if (!Double.isFinite(value)) {
                continue;
            }
            var candidate = new StatisticsExtremum(value, stepCount);
            if (mode.includesMinimum()) {
                minimumValues.merge(key, candidate,
                        (existing, incoming) -> (incoming.value() < existing.value()) ? incoming : existing);
            }
            if (mode.includesMaximum()) {
                maximumValues.merge(key, candidate,
                        (existing, incoming) -> (incoming.value() > existing.value()) ? incoming : existing);
            }
        }
    }

    synchronized StatisticsExtrema snapshot() {
        return new StatisticsExtrema(minimumValues, maximumValues);
    }

}

