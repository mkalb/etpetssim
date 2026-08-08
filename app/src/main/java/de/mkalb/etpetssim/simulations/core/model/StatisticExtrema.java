package de.mkalb.etpetssim.simulations.core.model;

import java.util.*;

/**
 * Immutable minimum and maximum extrema tracked for metric keys.
 *
 * @param minimumValues minimum extrema by metric key
 * @param maximumValues maximum extrema by metric key
 */
public record StatisticExtrema(
        Map<String, StatisticExtremum> minimumValues,
        Map<String, StatisticExtremum> maximumValues) {

    private static final StatisticExtrema EMPTY = new StatisticExtrema(Map.of(), Map.of());

    public StatisticExtrema {
        minimumValues = Collections.unmodifiableMap(new LinkedHashMap<>(minimumValues));
        maximumValues = Collections.unmodifiableMap(new LinkedHashMap<>(maximumValues));
    }

    public static StatisticExtrema empty() {
        return EMPTY;
    }

}

