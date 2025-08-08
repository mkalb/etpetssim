package de.mkalb.etpetssim.engine.model;

/**
 * Immutable timing statistics for simulation steps (in milliseconds).
 *
 * @param current duration of the most recent step
 * @param min     minimum step duration
 * @param max     maximum step duration
 * @param sum     sum of all step durations
 * @param avg     average step duration
 */
public record StepTimingStatistics(
        long current,
        long min,
        long max,
        long sum,
        long avg
) {

    /**
     * Returns a {@code StepTimingStatistics} instance with all values set to {@code 0}.
     * <p>
     * This can be used to represent the absence of any timing data,
     * for example before any simulation steps have been executed.
     *
     * @return a {@code StepTimingStatistics} record with all fields set to {@code 0}
     */
    public static StepTimingStatistics empty() {
        return new StepTimingStatistics(0, 0, 0, 0, 0);
    }

}
