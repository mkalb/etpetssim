package de.mkalb.etpetssim.simulations.core.model;

/**
 * Immutable point-in-time record of a single extremum: the metric value and the step at which it occurred.
 *
 * @param value     the minimum or maximum metric value
 * @param stepCount the simulation step count at which this extremum was first recorded
 */
public record StatisticsExtremum(double value, int stepCount) {

    /**
     * Creates an extremum.
     *
     * @throws IllegalArgumentException if {@code value} is not finite or {@code stepCount} is negative
     */
    public StatisticsExtremum {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("value must be finite: " + value);
        }
        if (stepCount < 0) {
            throw new IllegalArgumentException("stepCount must be >= 0");
        }
    }

    /**
     * Returns {@code value} using Java's narrowing conversion to {@code int}.
     * Intended for metrics that represent integral cell counts.
     *
     * @return the metric value converted to {@code int}
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public int intValue() {
        return (int) value;
    }

}
