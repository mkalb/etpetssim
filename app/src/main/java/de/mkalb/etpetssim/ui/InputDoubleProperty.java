package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Represents a double property with a defined range.
 * <p>
 * This record encapsulates a {@link DoubleProperty} and enforces value constraints:
 * - The value must be between {@code min} and {@code max} (inclusive).
 * <p>
 * Use {@link #of(double, double, double)} to create instances.
 *
 * @param property the underlying {@link DoubleProperty}
 * @param min      the minimum allowed value (inclusive)
 * @param max      the maximum allowed value (inclusive)
 */
public record InputDoubleProperty(DoubleProperty property, double min, double max) {

    /**
     * Constructs an {@code InputDoubleProperty} with the given property and range.
     * <p>
     * Validates that {@code min < max} and the property's value is valid.
     *
     * @param property the underlying {@link DoubleProperty}
     * @param min      the minimum allowed value
     * @param max      the maximum allowed value
     * @throws IllegalArgumentException if any argument is invalid
     */
    public InputDoubleProperty {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        if (!isValidValue(property.getValue(), min, max)) {
            throw new IllegalArgumentException("Initial value is not valid: " + property.getValue());
        }
    }

    /**
     * Creates a new {@code InputDoubleProperty} with the specified initial value and range.
     * <p>
     * Validates all arguments.
     *
     * @param initialValue the initial value
     * @param min          the minimum allowed value
     * @param max          the maximum allowed value
     * @return a new {@code InputDoubleProperty}
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static InputDoubleProperty of(double initialValue, double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        if (!isValidValue(initialValue, min, max)) {
            throw new IllegalArgumentException("Initial value is not valid: " + initialValue);
        }

        DoubleProperty property = new SimpleDoubleProperty(initialValue) {
            @Override
            public void set(double newValue) {
                if (!isValidValue(newValue, min, max)) {
                    AppLogger.error("InputDoubleProperty: Invalid value set: " + newValue +
                            " (min=" + min + ", max=" + max + ")");
                }
                super.set(newValue);
            }
        };
        return new InputDoubleProperty(property, min, max);
    }

    /**
     * Adjusts a value by clamping it to the range.
     *
     * @param newValue the value to adjust
     * @param min      the minimum allowed value
     * @param max      the maximum allowed value
     * @return the adjusted value
     */
    static double adjustValue(double newValue, double min, double max) {
        return Math.max(min, Math.min(max, newValue));
    }

    /**
     * Checks if a value is valid for the given range.
     *
     * @param value the value to check
     * @param min   the minimum allowed value
     * @param max   the maximum allowed value
     * @return {@code true} if the value is valid, {@code false} otherwise
     */
    static boolean isValidValue(double value, double min, double max) {
        return (value >= min) && (value <= max);
    }

    /**
     * Returns the property as an {@link ObjectProperty}.
     *
     * @return the property as an {@link ObjectProperty}
     */
    public ObjectProperty<Double> asObjectProperty() {
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
    public double getValue() {
        return property.getValue();
    }

    /**
     * Sets the property value.
     *
     * @param value the value to set
     */
    public void setValue(double value) {
        property.setValue(value);
    }

    /**
     * Checks if the current value is valid.
     *
     * @return {@code true} if the value is valid, {@code false} otherwise
     */
    public boolean isValid() {
        return isValidValue(getValue(), min, max);
    }

    /**
     * Checks if the current value equals {@code min}.
     *
     * @return {@code true} if the value is {@code min}, {@code false} otherwise
     */
    @SuppressWarnings("FloatingPointEquality")
    public boolean isMin() {
        return getValue() == min;
    }

    /**
     * Checks if the current value equals {@code max}.
     *
     * @return {@code true} if the value is {@code max}, {@code false} otherwise
     */
    @SuppressWarnings("FloatingPointEquality")
    public boolean isMax() {
        return getValue() == max;
    }

    /**
     * Adjusts a value using the current range.
     *
     * @param newValue the value to adjust
     * @return the adjusted value
     */
    public double adjustValue(double newValue) {
        return adjustValue(newValue, min, max);
    }

}
