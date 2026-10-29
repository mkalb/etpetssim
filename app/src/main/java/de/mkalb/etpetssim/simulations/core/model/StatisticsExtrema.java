package de.mkalb.etpetssim.simulations.core.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable minimum and maximum extrema tracked for metric keys.
 *
 * @param minimumValues minimum extrema by metric key
 * @param maximumValues maximum extrema by metric key
 */
public record StatisticsExtrema(
        Map<String, StatisticsExtremum> minimumValues,
        Map<String, StatisticsExtremum> maximumValues) {

    private static final StatisticsExtrema EMPTY = new StatisticsExtrema(Map.of(), Map.of());

    public StatisticsExtrema {
        minimumValues = Collections.unmodifiableMap(new LinkedHashMap<>(minimumValues));
        maximumValues = Collections.unmodifiableMap(new LinkedHashMap<>(maximumValues));
    }

    /**
     * Returns an extrema snapshot without tracked values.
     *
     * @return the empty extrema snapshot
     */
    public static StatisticsExtrema empty() {
        return EMPTY;
    }

}

