package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.*;
import java.util.function.*;

/**
 * Represents an enum property with a defined set of valid values and a display name provider.
 * <p>
 * This record encapsulates an {@link ObjectProperty} for an enum type, enforces that only values
 * from a specified list of valid values can be set, and provides a function to map enum values to display names.
 * <p>
 * Use {@link #of(Enum, List, Function)} or {@link #of(Enum, Class, Function)} to create instances with validation and display name mapping.
 *
 * @param <E> the enum type
 * @param property    the underlying {@link ObjectProperty} for the enum
 * @param validValues the list of valid enum values (must not be empty)
 * @param displayNameProvider a function mapping enum values to their display names
 */
public record InputEnumProperty<E extends Enum<E>>(ObjectProperty<E> property,
                                                   List<E> validValues,
                                                   Function<E, String> displayNameProvider) {

    /**
     * Constructs an {@code InputEnumProperty} with the given property, valid values, and display name provider.
     * <p>
     * Validates that {@code validValues} is not empty and that the property's value is valid.
     * Makes a defensive copy of {@code validValues}.
     *
     * @param property the underlying {@link ObjectProperty} for the enum
     * @param validValues the list of valid enum values
     * @param displayNameProvider a function mapping enum values to their display names
     * @throws IllegalArgumentException if {@code validValues} is empty or the property's value is not valid
     */
    public InputEnumProperty(ObjectProperty<E> property,
                             List<E> validValues,
                             Function<E, String> displayNameProvider) {
        if (validValues.isEmpty()) {
            throw new IllegalArgumentException("validValues must not be empty");
        }
        if (!validValues.contains(property.get())) {
            throw new IllegalArgumentException("Initial value must be in validValues");
        }
        this.property = property;
        this.validValues = List.copyOf(validValues);
        this.displayNameProvider = displayNameProvider;
    }

    /**
     * Creates a new {@code InputEnumProperty} with the specified initial value, valid values, and display name provider.
     * <p>
     * Validates all arguments and ensures only valid values can be set.
     *
     * @param initialValue the initial enum value
     * @param validValues  the list of valid enum values
     * @param displayNameProvider a function mapping enum values to their display names
     * @return a new {@code InputEnumProperty}
     * @throws IllegalArgumentException if {@code validValues} is empty or the initial value is not valid
     */
    public static <E extends Enum<E>> InputEnumProperty<E> of(E initialValue,
                                                              List<E> validValues,
                                                              Function<E, String> displayNameProvider) {
        if (validValues.isEmpty()) {
            throw new IllegalArgumentException("validValues must not be empty");
        }
        if (!isValidValue(initialValue, validValues)) {
            throw new IllegalArgumentException("Initial value must be in validValues");
        }

        List<E> copiedValidValues = List.copyOf(validValues);
        ObjectProperty<E> property = new SimpleObjectProperty<>(initialValue) {
            @Override
            public void set(E newValue) {
                if (!isValidValue(newValue, copiedValidValues)) {
                    AppLogger.error("InputEnumProperty: Invalid value set: " + newValue +
                            ". Valid values are: " + copiedValidValues);
                }
                super.set(newValue);
            }
        };
        return new InputEnumProperty<>(property, copiedValidValues, displayNameProvider);
    }

    /**
     * Creates a new {@code InputEnumProperty} with the specified initial value, all values of the given enum type as valid values, and a display name provider.
     * <p>
     * This method uses {@code enumClass.getEnumConstants()} to determine the valid values.
     *
     * @param initialValue the initial enum value
     * @param enumClass    the {@link Class} object of the enum type
     * @param displayNameProvider a function mapping enum values to their display names
     * @return a new {@code InputEnumProperty} with all enum values as valid values
     * @throws IllegalArgumentException if the initial value is not a valid enum constant
     */
    public static <E extends Enum<E>> InputEnumProperty<E> of(E initialValue,
                                                              Class<E> enumClass,
                                                              Function<E, String> displayNameProvider) {
        List<E> validValues = List.of(enumClass.getEnumConstants());
        return of(initialValue, validValues, displayNameProvider);
    }

    /**
     * Checks if a value is valid for the given list of valid values.
     *
     * @param value       the value to check
     * @param validValues the list of valid values
     * @return {@code true} if the value is valid, {@code false} otherwise
     */
    static <E extends Enum<E>> boolean isValidValue(E value, List<E> validValues) {
        return validValues.contains(value);
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
     * @return the current enum value
     */
    public E getValue() {
        return property.get();
    }

    /**
     * Sets the property value.
     *
     * @param value the enum value to set
     */
    public void setValue(E value) {
        property.set(value);
    }

    /**
     * Checks if the current value is valid.
     *
     * @return {@code true} if the value is valid, {@code false} otherwise
     */
    public boolean isValid() {
        return isValidValue(getValue(), validValues);
    }

    /**
     * Returns the index of the current value within the list of valid values.
     *
     * @return the index of the value, or -1 if not found
     */
    public int getIndex() {
        return validValues.indexOf(getValue());
    }

    /**
     * Returns the maximum index (size of the valid values list minus one).
     *
     * @return the maximum index
     */
    public int getMaxIndex() {
        return validValues.size() - 1;
    }

    /**
     * Checks if the current value of the property is equal to the specified value.
     *
     * @param value the value to compare with the current property value
     * @return {@code true} if the current value equals the specified value, {@code false} otherwise
     */
    public boolean isValue(E value) {
        return getValue() == value;
    }

    /**
     * Returns an unmodifiable list of valid enum values for this property.
     * <p>
     * Only values from this list are allowed to be set for the property.
     *
     * @return the list of valid enum values
     */
    public List<E> getValidValues() {
        return validValues;
    }

}
