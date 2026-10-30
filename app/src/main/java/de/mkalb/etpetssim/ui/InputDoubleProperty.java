package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Represents a double property with a defined range.
 *
 * <p>Encapsulates a {@link DoubleProperty} and enforces value constraints:
 * <ul>
 *   <li>The value must be between {@code min} and {@code max} (inclusive).</li>
 * </ul>
 *
 * <p>Use {@link #of(double, double, double)} to create instances.
 *
 * <p>The record wraps the given property without copying it; bindings and listeners on the property stay effective.
 *
 * @param property the underlying {@link DoubleProperty}
 * @param min      the minimum allowed value (inclusive)
 * @param max      the maximum allowed value (inclusive)
 */
public record InputDoubleProperty(DoubleProperty property, double min, double max) {

    /**
     * Creates an input property with a range.
     *
     * @throws IllegalArgumentException if {@code min} is not less than {@code max}, or if the value of
     *                                  {@code property} is not within the range
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
     *
     * <p>Validation is performed by the canonical constructor.
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
                    AppLogger.errorf("InputDoubleProperty: Invalid value set: %s (min=%s, max=%s)",
                            newValue, min, max);
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

    public double getValue() {
        return property.get();
    }

    /**
     * Sets the property value.
     *
     * <p>An invalid value is logged via {@link AppLogger} and still set, if this instance was created by a factory
     * method.
     *
     * @param value the value to set
     */
    public void setValue(double value) {
        property.set(value);
    }

    /**
     * Clamps a value to the range between {@code min} and {@code max}.
     *
     * @param newValue the value to adjust
     * @return the adjusted value
     */
    public double adjustValue(double newValue) {
        return Math.clamp(newValue, min, max);
    }

}
