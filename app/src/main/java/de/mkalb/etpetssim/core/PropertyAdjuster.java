package de.mkalb.etpetssim.core;

import javafx.beans.property.*;

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
     * Clamps a double value to the specified range.
     * <p>
     * If {@code newValue} is less than {@code min}, returns {@code min}.
     * If {@code newValue} is greater than {@code max}, returns {@code max}.
     * Otherwise, returns {@code newValue}.
     *
     * @param newValue the value to clamp
     * @param min      the minimum allowed value (inclusive)
     * @param max      the maximum allowed value (inclusive)
     * @return the clamped value within the range {@code [min, max]}
     */
    public static double adjustDoubleValue(double newValue, double min, double max) {
        return Math.max(min, Math.min(max, newValue));
    }

    /**
     * Creates a {@link DoubleProperty} that clamps its value to the specified integer range whenever it is set.
     * <p>
     * The initial value is normalized using {@link #adjustDoubleValue(double, double, double)}.
     * Any subsequent calls to {@code set()} will also be normalized.
     *
     * @param initialValue the initial value for the property
     * @param min          the minimum allowed value (inclusive)
     * @param max          the maximum allowed value (inclusive)
     * @return a {@link DoubleProperty} with automatic clamping to the integer range
     */
    public static DoubleProperty createDoublePropertyWithIntRange(int initialValue, int min, int max) {
        return new SimpleDoubleProperty(adjustDoubleValue(initialValue, min, max)) {
            @Override
            public void set(double newValue) {
                super.set(adjustDoubleValue(newValue, min, max));
            }
        };
    }

    /**
     * Creates a {@link DoubleProperty} that clamps its value to the specified double range whenever it is set.
     * <p>
     * The initial value is normalized using {@link #adjustDoubleValue(double, double, double)}.
     * Any subsequent calls to {@code set()} will also be normalized.
     *
     * @param initialValue the initial value for the property
     * @param min          the minimum allowed value (inclusive)
     * @param max          the maximum allowed value (inclusive)
     * @return a {@link DoubleProperty} with automatic clamping to the double range
     */
    public static DoubleProperty createAdjustedDoubleProperty(double initialValue, double min, double max) {
        return new SimpleDoubleProperty(adjustDoubleValue(initialValue, min, max)) {
            @Override
            public void set(double newValue) {
                super.set(adjustDoubleValue(newValue, min, max));
            }
        };
    }

}
