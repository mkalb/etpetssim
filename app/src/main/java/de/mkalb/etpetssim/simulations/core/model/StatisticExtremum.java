package de.mkalb.etpetssim.simulations.core.model;

/**
 * Immutable point-in-time record of a single extremum: the metric value and the step at which it occurred.
 *
 * @param value     the minimum or maximum metric value
 * @param stepCount the simulation step at which this extremum was first recorded
 */
public record StatisticExtremum(double value, long stepCount) {

    /**
     * Returns {@code value} truncated to {@code int}, for metrics that are always integral cell counts.
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public int intValue() {
        return (int) value;
    }

}
