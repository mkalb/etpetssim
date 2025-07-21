package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;

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
public record ExtendedIntegerProperty(IntegerProperty property, int min, int max, int step) {

    /**
     * Constructs an {@code ExtendedIntegerProperty} with the given property, range, and step.
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
    public ExtendedIntegerProperty {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("step must be positive");
        }
        if (((max - min) % step) != 0) {
            throw new IllegalArgumentException("The range (max - min) must be divisible by step");
        }
        if (!isValidValue(property.getValue(), min, max, step)) {
            throw new IllegalArgumentException("Initial value is not valid: " + property.getValue());
        }
    }

    /**
     * Creates a new {@code ExtendedIntegerProperty} with the specified initial value, range, and step.
     * <p>
     * Validates all arguments.
     *
     * @param initialValue the initial value
     * @param min          the minimum allowed value
     * @param max          the maximum allowed value
     * @param step         the step size
     * @return a new {@code ExtendedIntegerProperty}
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static ExtendedIntegerProperty of(int initialValue, int min, int max, int step) {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("step must be positive");
        }
        if (((max - min) % step) != 0) {
            throw new IllegalArgumentException("The range (max - min) must be divisible by step");
        }
        if (!isValidValue(initialValue, min, max, step)) {
            throw new IllegalArgumentException("Initial value is not valid: " + initialValue);
        }

        IntegerProperty property = new SimpleIntegerProperty(initialValue) {
            @Override
            public void set(int newValue) {
                if (!isValidValue(newValue, min, max, step)) {
                    AppLogger.error("ExtendedIntegerProperty: Invalid value set: " + newValue +
                            " (min=" + min + ", max=" + max + ", step=" + step + ")");
                }
                super.set(newValue);
            }
        };
        return new ExtendedIntegerProperty(property, min, max, step);
    }

    /**
     * Adjusts a value by clamping it to the range and snapping it to the nearest valid step.
     *
     * @param newValue the value to adjust
     * @param min      the minimum allowed value
     * @param max      the maximum allowed value
     * @param step     the step size
     * @return the adjusted value
     */
    static int adjustValue(int newValue, int min, int max, int step) {
        int clamped = Math.max(min, Math.min(max, newValue));
        return min + (Math.round((float) (clamped - min) / step) * step);
    }

    /**
     * Checks if a value is valid for the given range and step.
     *
     * @param value the value to check
     * @param min   the minimum allowed value
     * @param max   the maximum allowed value
     * @param step  the step size
     * @return {@code true} if the value is valid, {@code false} otherwise
     */
    static boolean isValidValue(int value, int min, int max, int step) {
        return (value >= min) && (value <= max) && (((value - min) % step) == 0);
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
        return property.getValue();
    }

    /**
     * Sets the property value.
     *
     * @param value the value to set
     */
    public void setValue(int value) {
        property.setValue(value);
    }

    /**
     * Checks if the current value is valid.
     *
     * @return {@code true} if the value is valid, {@code false} otherwise
     */
    public boolean isValid() {
        return isValidValue(getValue(), min, max, step);
    }

    /**
     * Checks if the current value equals {@code min}.
     *
     * @return {@code true} if the value is {@code min}, {@code false} otherwise
     */
    public boolean isMin() {
        return getValue() == min;
    }

    /**
     * Checks if the current value equals {@code max}.
     *
     * @return {@code true} if the value is {@code max}, {@code false} otherwise
     */
    public boolean isMax() {
        return getValue() == max;
    }

    /**
     * Checks if the step size is 1.
     *
     * @return {@code true} if {@code step} is 1, {@code false} otherwise
     */
    public boolean isStepOne() {
        return step == 1;
    }

    /**
     * Returns the index of the current value within the range, based on the step size.
     *
     * @return the index of the value
     */
    public int getIndex() {
        return (getValue() - min) / step;
    }

    /**
     * Returns the maximum index (number of steps in the range).
     *
     * @return the maximum index
     */
    public int getMaxIndex() {
        return (max - min) / step;
    }

    /**
     * Adjusts a value using the current range and step.
     *
     * @param newValue the value to adjust
     * @return the adjusted value
     */
    public int adjustValue(int newValue) {
        return adjustValue(newValue, min, max, step);
    }

}
