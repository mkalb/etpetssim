package de.mkalb.etpetssim.engine.executor;

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
     * Creates timing statistics with consistent values.
     *
     * @throws IllegalArgumentException if a value is negative, if {@code minNanos > maxNanos}, if
     *                                  {@code sumNanos} is {@code 0} but another value is not, or if
     *                                  {@code currentNanos} or {@code avgNanos} is outside
     *                                  {@code minNanos..maxNanos}
     */
    public StepTimingStatistics {
        if ((currentNanos < 0) || (minNanos < 0) || (maxNanos < 0) || (sumNanos < 0) || (avgNanos < 0)) {
            throw new IllegalArgumentException("all values must be >= 0, but was: "
                    + formatValues(currentNanos, minNanos, maxNanos, sumNanos, avgNanos));
        }
        if (minNanos > maxNanos) {
            throw new IllegalArgumentException("minNanos must be <= maxNanos, but was: minNanos=" + minNanos
                    + ", maxNanos=" + maxNanos);
        }
        if (sumNanos == 0) {
            if ((currentNanos != 0) || (minNanos != 0) || (maxNanos != 0) || (avgNanos != 0)) {
                throw new IllegalArgumentException("all values must be 0 if sumNanos is 0, but was: "
                        + formatValues(currentNanos, minNanos, maxNanos, sumNanos, avgNanos));
            }
        } else {
            if ((currentNanos < minNanos) || (currentNanos > maxNanos)) {
                throw new IllegalArgumentException("currentNanos must be in [minNanos, maxNanos], but was: "
                        + "currentNanos=" + currentNanos + ", minNanos=" + minNanos + ", maxNanos=" + maxNanos);
            }
            if ((avgNanos < minNanos) || (avgNanos > maxNanos)) {
                throw new IllegalArgumentException("avgNanos must be in [minNanos, maxNanos], but was: "
                        + "avgNanos=" + avgNanos + ", minNanos=" + minNanos + ", maxNanos=" + maxNanos);
            }
        }
    }

    /**
     * Returns a {@code StepTimingStatistics} instance with all values set to {@code 0}.
     *
     * <p>This can be used to represent the absence of any timing data, for example before any simulation steps have
     * been executed.
     *
     * @return a {@code StepTimingStatistics} record with all fields set to {@code 0} nanoseconds
     */
    public static StepTimingStatistics empty() {
        return new StepTimingStatistics(0, 0, 0, 0, 0);
    }

    private static String formatValues(long currentNanos, long minNanos, long maxNanos, long sumNanos,
                                       long avgNanos) {
        return "currentNanos=" + currentNanos + ", minNanos=" + minNanos + ", maxNanos=" + maxNanos
                + ", sumNanos=" + sumNanos + ", avgNanos=" + avgNanos;
    }

}
