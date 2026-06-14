package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;

/**
 * Represents an integer property with a defined range and step size.
 * <p>
 * This record encapsulates a {@link IntegerProperty} and enforces value constraints:
 * - The value must be between {@code min} and {@code max} (inclusive).
 * - The value must be a multiple of {@code step} starting from {@code min}.
 * <p>
 * Use {@link #of(int, int, int, int)} to create instances with validation.
 *
 * @param property the underlying {@link IntegerProperty}
 * @param min      the minimum allowed value (inclusive)
 * @param max      the maximum allowed value (inclusive)
 * @param step     the step size for valid values (must be positive)
 */
public record InputIntegerProperty(IntegerProperty property, int min, int max, int step) {

    /**
     * Constructs an {@code InputIntegerProperty} with the given property, range, and step.
     * <p>
     * Validates that {@code min < max}, {@code step > 0}, the range is divisible by {@code step},
     * and the property's value is valid.
     *
     * @param property the underlying {@link IntegerProperty}
     * @param min      the minimum allowed value
     * @param max      the maximum allowed value
     * @param step     the step size
     * @throws IllegalArgumentException if any argument is invalid
     */
    public InputIntegerProperty {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("step must be positive");
        }
        if (((max - min) % step) != 0) {
            throw new IllegalArgumentException("The range (max - min) must be divisible by step");
        }
        if (isInvalidValue(property.get(), min, max, step)) {
            throw new IllegalArgumentException("Initial value is not valid: " + property.get());
        }
    }

    /**
     * Creates a new {@code InputIntegerProperty} with the specified initial value, range, and step.
     * <p>
     * Validation is performed by the canonical constructor.
     *
     * @param initialValue the initial value
     * @param min          the minimum allowed value
     * @param max          the maximum allowed value
     * @param step         the step size
     * @return a new {@code InputIntegerProperty}
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static InputIntegerProperty of(int initialValue, int min, int max, int step) {
        var property = new SimpleIntegerProperty(initialValue) {
            @Override
            public void set(int newValue) {
                if (isInvalidValue(newValue, min, max, step)) {
                    AppLogger.error("InputIntegerProperty: Invalid value set: " + newValue +
                            " (min=" + min + ", max=" + max + ", step=" + step + ")");
                }
                // The value is set even if it is invalid.
                super.set(newValue);
            }
        };
        return new InputIntegerProperty(property, min, max, step);
    }

    /**
     * Checks if a value is invalid for the given range and step.
     *
     * @param value the value to check
     * @param min   the minimum allowed value
     * @param max   the maximum allowed value
     * @param step  the step size
     * @return {@code true} if the value is invalid, {@code false} otherwise
     */
    static boolean isInvalidValue(int value, int min, int max, int step) {
        return (value < min) || (value > max) || (((value - min) % step) != 0);
    }

    /**
     * Returns the property as an {@link ObjectProperty}.
     *
     * @return the property as an {@link ObjectProperty}
     */
    public ObjectProperty<Integer> asObjectProperty() {
        return property.asObject();
    }

    /**
     * Returns a {@link StringBinding} for the property using the given format.
     *
     * @param format the format string
     * @return a {@link StringBinding} representing the property value
     */
    public StringBinding asStringBinding(String format) {
        return property.asString(format);
    }

    /**
     * Gets the current value of the property.
     *
     * @return the current value
     */
    public int getValue() {
        return property.get();
    }

    /**
     * Sets the property value.
     *
     * @param value the value to set
     */
    public void setValue(int value) {
        property.set(value);
    }

    /**
     * Adjusts a value using the current range and step.
     *
     * @param newValue the value to adjust
     * @return the adjusted value
     */
    public int adjustValue(int newValue) {
        int clamped = Math.clamp(newValue, min, max);
        int delta = clamped - min;
        int lowerOffset = (delta / step) * step;
        int upperOffset = Math.min(lowerOffset + step, max - min);

        if ((delta - lowerOffset) <= (upperOffset - delta)) {
            return min + lowerOffset;
        }
        return min + upperOffset;
    }

}
