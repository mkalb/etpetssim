package de.mkalb.etpetssim.ui;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Represents a double property with an integer-based range.
 * <p>
 * This record encapsulates a {@link DoubleProperty} and enforces value constraints:
 * - The value must be between {@code min} and {@code max} (inclusive).
 * <p>
 * Use {@link #of(int, int, int)} to create instances with validation.
 *
 * @param property the underlying {@link DoubleProperty}
 * @param min      the minimum allowed value (inclusive, integer)
 * @param max      the maximum allowed value (inclusive, integer)
 */
public record ExtendedDoublePropertyIntRange(DoubleProperty property, int min, int max) {

    /**
     * Constructs a {@code ExtendedDoublePropertyIntRange} with the given property and integer range.
     * <p>
     * Validates that {@code min < max} and the property's value is valid.
     *
     * @param property the underlying {@link DoubleProperty}
     * @param min      the minimum allowed value
     * @param max      the maximum allowed value
     * @throws IllegalArgumentException if any argument is invalid
     */
    public ExtendedDoublePropertyIntRange {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        if (!isValidValue(property.getValue(), min, max)) {
            throw new IllegalArgumentException("Initial value is not valid: " + property.getValue());
        }
    }

    /**
     * Creates a new {@code ExtendedDoublePropertyIntRange} with the specified initial value and integer range.
     * <p>
     * Validates all arguments.
     *
     * @param initialValue the initial value
     * @param min          the minimum allowed value
     * @param max          the maximum allowed value
     * @return a new {@code ExtendedDoublePropertyIntRange}
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static ExtendedDoublePropertyIntRange of(int initialValue, int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        if (!isValidValue(initialValue, min, max)) {
            throw new IllegalArgumentException("Initial value is not valid: " + initialValue);
        }

        DoubleProperty property = new SimpleDoubleProperty(adjustValue(initialValue, min, max)) {
            @Override
            public void set(double newValue) {
                super.set(adjustValue(newValue, min, max));
            }
        };
        return new ExtendedDoublePropertyIntRange(property, min, max);
    }

    /**
     * Adjusts a value by clamping it to the range.
     *
     * @param newValue the value to adjust
     * @param min      the minimum allowed value
     * @param max      the maximum allowed value
     * @return the adjusted value
     */
    static double adjustValue(double newValue, int min, int max) {
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
    static boolean isValidValue(double value, int min, int max) {
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
