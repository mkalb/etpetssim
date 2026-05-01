package de.mkalb.etpetssim.engine.model;

/**
 * Immutable timing statistics for simulation steps (in nanoseconds).
 *
 * @param currentNanos duration of the most recent step
 * @param minNanos     minimum step duration
 * @param maxNanos     maximum step duration
 * @param sumNanos     sum of all step durations
 * @param avgNanos     average step duration
 */
public record StepTimingStatistics(
        long currentNanos,
        long minNanos,
        long maxNanos,
        long sumNanos,
        long avgNanos
) {

    /**
     * Returns a {@code StepTimingStatistics} instance with all values set to {@code 0}.
     * <p>
     * This can be used to represent the absence of any timing data,
     * for example before any simulation steps have been executed.
     *
     * @return a {@code StepTimingStatistics} record with all fields set to {@code 0} nanoseconds
     */
    public static StepTimingStatistics empty() {
        return new StepTimingStatistics(0, 0, 0, 0, 0);
    }

}
