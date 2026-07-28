package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;

import java.util.*;

/**
 * Immutable sampled statistics at one step index.
 *
 * @param stepCount simulation step index of this sample
 * @param stepTimingStatistics timing snapshot for the sampled step
 * @param values sampled metric values by metric key
 */
public record StatisticSample(
        int stepCount,
        StepTimingStatistics stepTimingStatistics,
        Map<String, Double> values) {

    public StatisticSample {
        if (stepCount < 0) {
            throw new IllegalArgumentException("stepCount must be >= 0");
        }
        values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

}

