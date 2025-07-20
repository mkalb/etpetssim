package de.mkalb.etpetssim.core;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Utility class for adjusting property values with clamping, snapping, and normalization logic.
 */
public final class PropertyAdjuster {

    /**
     * Private constructor to prevent instantiation.
     */
    private PropertyAdjuster() {
    }

    /**
     * Adjusts an integer value by clamping it to the specified range and snapping it to the nearest step.
     * <p>
     * The value is first clamped to the interval {@code [min, max]}, then snapped to the nearest multiple
     * of {@code step} starting from {@code min}.
     *
     * @param newValue the value to adjust
     * @param min      the minimum allowed value (inclusive)
     * @param max      the maximum allowed value (inclusive)
     * @param step     the step size for snapping
     * @return the adjusted value, clamped and snapped to the nearest valid step
     */
    public static int adjustIntValue(int newValue, int min, int max, int step) {
        int clamped = Math.max(min, Math.min(max, newValue));
        return min + (Math.round((float) (clamped - min) / step) * step);
    }

    /**
     * Creates an {@link IntegerProperty} that automatically clamps and snaps its value
     * to the specified range and step whenever it is set.
     * <p>
     * The initial value is normalized using {@link #adjustIntValue(int, int, int, int)}.
     * Any subsequent calls to {@code set()} will also be normalized.
     *
     * @param initialValue the initial value for the property
     * @param min          the minimum allowed value (inclusive)
     * @param max          the maximum allowed value (inclusive)
     * @param step         the step size for snapping
     * @return an {@link IntegerProperty} with automatic clamping and snapping
     */
    public static IntegerProperty createAdjustedIntProperty(
            int initialValue, int min, int max, int step) {
        return new SimpleIntegerProperty(adjustIntValue(initialValue, min, max, step)) {
            @Override
            public void set(int newValue) {
                super.set(adjustIntValue(newValue, min, max, step));
            }
        };
    }

}
