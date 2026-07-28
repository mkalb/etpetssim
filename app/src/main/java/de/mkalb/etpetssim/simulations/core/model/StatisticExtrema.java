package de.mkalb.etpetssim.simulations.core.model;

import java.util.*;

/**
 * Immutable minimum and maximum values tracked for metric keys.
 *
 * @param minimumValues minimum values by metric key
 * @param maximumValues maximum values by metric key
 */
public record StatisticExtrema(
        Map<String, Double> minimumValues,
        Map<String, Double> maximumValues) {

    private static final StatisticExtrema EMPTY = new StatisticExtrema(Map.of(), Map.of());

    public StatisticExtrema {
        minimumValues = Collections.unmodifiableMap(new LinkedHashMap<>(minimumValues));
        maximumValues = Collections.unmodifiableMap(new LinkedHashMap<>(maximumValues));
    }

    public static StatisticExtrema empty() {
        return EMPTY;
    }

}

