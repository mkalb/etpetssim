package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.binding.*;
import javafx.beans.property.*;

import java.util.*;
import java.util.function.*;

/**
 * Represents an enum property with a defined set of valid values and a display name provider.
 * <p>
 * This record encapsulates an {@link ObjectProperty} for an enum type and provides
 * validation against a specified list of valid values.
 * <p>
 * Invalid values are logged via {@link AppLogger} and still set on the property.
 * <p>
 * Use {@link #of(Enum, List, Function)} or {@link #of(Enum, Class, Function)} to create instances.
 *
 * @param <E>                 the enum type
 * @param property            the underlying {@link ObjectProperty} for the enum
 * @param validValues         the list of valid enum values (must not be empty)
 * @param displayNameProvider a function mapping enum values to their display names
 */
public record InputEnumProperty<E extends Enum<E>>(ObjectProperty<E> property,
                                                   List<E> validValues,
                                                   Function<E, String> displayNameProvider) {

    /**
     * Constructs an {@code InputEnumProperty} with the given property, valid values, and display name provider.
     * <p>
     * Validates that {@code validValues} is not empty, does not contain duplicates,
     * and that the property's value is valid.
     * Makes a defensive copy of {@code validValues}.
     *
     * @param property            the underlying {@link ObjectProperty} for the enum
     * @param validValues         the list of valid enum values
     * @param displayNameProvider a function mapping enum values to their display names
     * @throws IllegalArgumentException if any argument is invalid
     */
    public InputEnumProperty {
        if (validValues.isEmpty()) {
            throw new IllegalArgumentException("validValues must not be empty");
        }
        Set<E> uniqueValues = new LinkedHashSet<>(validValues.size());
        for (E validValue : validValues) {
            if (!uniqueValues.add(validValue)) {
                throw new IllegalArgumentException("validValues must not contain duplicates");
            }
        }
        if (isInvalidValue(property.get(), validValues)) {
            throw new IllegalArgumentException("Initial value is not valid: " + property.get());
        }
        validValues = List.copyOf(validValues);
    }

    /**
     * Creates a new {@code InputEnumProperty} with the specified initial value, valid values, and display name provider.
     * <p>
     * Validation is performed by the canonical constructor.
     *
     * @param initialValue        the initial enum value
     * @param validValues         the list of valid enum values
     * @param displayNameProvider a function mapping enum values to their display names
     * @return a new {@code InputEnumProperty}
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static <E extends Enum<E>> InputEnumProperty<E> of(E initialValue,
                                                              List<E> validValues,
                                                              Function<E, String> displayNameProvider) {
        List<E> copiedValidValues = List.copyOf(validValues);
        var property = new SimpleObjectProperty<>(initialValue) {
            @Override
            public void set(E newValue) {
                if (isInvalidValue(newValue, copiedValidValues)) {
                    AppLogger.error("InputEnumProperty: Invalid value set: " + newValue +
                            ". Valid values are: " + copiedValidValues);
                }
                // The value is set even if it is invalid.
                super.set(newValue);
            }
        };
        return new InputEnumProperty<>(property, copiedValidValues, displayNameProvider);
    }

    /**
     * Creates a new {@code InputEnumProperty} with all values of the given enum type and a display name provider.
     * <p>
     * This method uses {@code enumClass.getEnumConstants()} to determine the valid values.
     *
     * @param initialValue        the initial enum value
     * @param enumClass           the {@link Class} object of the enum type
     * @param displayNameProvider a function mapping enum values to their display names
     * @return a new {@code InputEnumProperty} with all enum values as valid values
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static <E extends Enum<E>> InputEnumProperty<E> of(E initialValue,
                                                              Class<E> enumClass,
                                                              Function<E, String> displayNameProvider) {
        return of(initialValue, List.of(enumClass.getEnumConstants()), displayNameProvider);
    }

    /**
     * Checks if a value is invalid for the given list of valid values.
     *
     * @param value       the value to check
     * @param validValues the list of valid values
     * @return {@code true} if the value is invalid, {@code false} otherwise
     */
    static <E extends Enum<E>> boolean isInvalidValue(E value, List<E> validValues) {
        return !validValues.contains(value);
    }

    /**
     * Returns a {@link StringBinding} for the property using the given format and display name provider.
     *
     * @param format the format string
     * @return a {@link StringBinding} representing the formatted display value
     */
    public StringBinding asStringBinding(String format) {
        return Bindings.createStringBinding(() -> format.formatted(displayNameProvider.apply(property.get())), property);
    }

    /**
     * Gets the current value of the property.
     *
     * @return the current value
     */
    public E getValue() {
        return property.get();
    }

    /**
     * Sets the property value.
     *
     * @param value the value to set
     */
    public void setValue(E value) {
        property.set(value);
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
     * Checks whether this record has more than one valid value.
     *
     * @return {@code true} if there is more than one valid value, {@code false} otherwise
     */
    public boolean hasMultipleValidValues() {
        return validValues.size() > 1;
    }

}
