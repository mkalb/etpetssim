package de.mkalb.etpetssim.engine.executor;

/**
 * Immutable timing statistics for simulation steps (in nanoseconds).
 *
 * @param currentNanos duration of the most recent step
 * @param minNanos     minimum step duration
 * @param maxNanos     maximum step duration
 * @param sumNanos     sum of all step durations
 * @param avgNanos     average step duration
 *                     <p>
 *                     The canonical constructor enforces non-negative values and validates
 *                     cross-field consistency (for example, {@code minNanos <= maxNanos}).
 */
public record StepTimingStatistics(
        long currentNanos,
        long minNanos,
        long maxNanos,
        long sumNanos,
        long avgNanos
) {

    public StepTimingStatistics {
        if ((currentNanos < 0) || (minNanos < 0) || (maxNanos < 0) || (sumNanos < 0) || (avgNanos < 0)) {
            throw new IllegalArgumentException("timing values must be >= 0");
        }
        if (minNanos > maxNanos) {
            throw new IllegalArgumentException("minNanos must be <= maxNanos");
        }
        if (sumNanos == 0) {
            if ((currentNanos != 0) || (minNanos != 0) || (maxNanos != 0) || (avgNanos != 0)) {
                throw new IllegalArgumentException("empty statistics must contain only zero values");
            }
        } else {
            if ((currentNanos < minNanos) || (currentNanos > maxNanos)) {
                throw new IllegalArgumentException("currentNanos must be between minNanos and maxNanos");
            }
            if ((avgNanos < minNanos) || (avgNanos > maxNanos)) {
                throw new IllegalArgumentException("avgNanos must be between minNanos and maxNanos");
            }
        }
    }

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
