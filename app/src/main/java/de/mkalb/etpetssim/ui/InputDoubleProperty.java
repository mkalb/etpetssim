package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;

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
        if (isInvalidValue(property.get(), min, max)) {
            throw new IllegalArgumentException("Initial value is not valid: " + property.get());
        }
    }

    /**
     * Creates a new {@code InputDoubleProperty} with the specified initial value and range.
     * <p>
     * Validation is performed by the canonical constructor.
     *
     * @param initialValue the initial value
     * @param min          the minimum allowed value
     * @param max          the maximum allowed value
     * @return a new {@code InputDoubleProperty}
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static InputDoubleProperty of(double initialValue, double min, double max) {
        var property = new SimpleDoubleProperty(initialValue) {
            @Override
            public void set(double newValue) {
                if (isInvalidValue(newValue, min, max)) {
                    AppLogger.error("InputDoubleProperty: Invalid value set: " + newValue +
                            " (min=" + min + ", max=" + max + ")");
                }
                // The value is set even if it is invalid.
                super.set(newValue);
            }
        };
        return new InputDoubleProperty(property, min, max);
    }

    /**
     * Checks if a value is invalid for the given range.
     *
     * @param value the value to check
     * @param min   the minimum allowed value
     * @param max   the maximum allowed value
     * @return {@code true} if the value is invalid, {@code false} otherwise
     */
    static boolean isInvalidValue(double value, double min, double max) {
        return (!(value >= min)) || (!(value <= max));
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
        return property.get();
    }

    /**
     * Sets the property value.
     *
     * @param value the value to set
     */
    public void setValue(double value) {
        property.set(value);
    }

    /**
     * Adjusts a value using the current range.
     *
     * @param newValue the value to adjust
     * @return the adjusted value
     */
    public double adjustValue(double newValue) {
        return Math.clamp(newValue, min, max);
    }

}
