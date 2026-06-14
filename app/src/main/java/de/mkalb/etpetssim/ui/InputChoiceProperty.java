package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.binding.*;
import javafx.beans.property.*;

import java.util.*;
import java.util.function.*;

/**
 * Represents a property with a defined set of valid values and a display name provider.
 * <p>
 * This record encapsulates an {@link ObjectProperty} for a single selected value and provides
 * validation against a specified list of valid values.
 * <p>
 * Invalid values are logged via {@link AppLogger} and still set on the property.
 * <p>
 * Use {@link #of(Object, List, Function)} to create instances.
 *
 * @param <T>                 the value type
 * @param property            the underlying {@link ObjectProperty} for the selected value
 * @param validValues         the list of valid values (must not be empty, contain nulls, or contain duplicates)
 * @param displayNameProvider a function mapping values to their display names
 */
public record InputChoiceProperty<T>(ObjectProperty<T> property,
                                     List<T> validValues,
                                     Function<T, String> displayNameProvider) {

    /**
     * Constructs an {@code InputChoiceProperty} with the given property, valid values, and display name provider.
     * <p>
     * Validates that {@code validValues} is not empty, does not contain duplicates,
     * and that the property's value is valid.
     * Makes a defensive copy of {@code validValues}.
     *
     * @param property            the underlying {@link ObjectProperty} for the selected value
     * @param validValues         the list of valid values
     * @param displayNameProvider a function mapping values to their display names
     * @throws IllegalArgumentException if any argument is invalid
     */
    public InputChoiceProperty {
        if (validValues.isEmpty()) {
            throw new IllegalArgumentException("validValues must not be empty");
        }
        Set<T> uniqueValues = new LinkedHashSet<>(validValues.size());
        for (T validValue : validValues) {
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
     * Creates a new {@code InputChoiceProperty} with the specified initial value, valid values, and display name provider.
     * <p>
     * Validation is performed by the canonical constructor.
     *
     * @param initialValue        the initial selected value
     * @param validValues         the list of valid values
     * @param displayNameProvider a function mapping values to their display names
     * @param <T>                 the value type
     * @return a new {@code InputChoiceProperty}
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static <T> InputChoiceProperty<T> of(T initialValue,
                                                List<T> validValues,
                                                Function<T, String> displayNameProvider) {
        List<T> copiedValidValues = List.copyOf(validValues);
        var property = new SimpleObjectProperty<>(initialValue) {
            @Override
            public void set(T newValue) {
                if (isInvalidValue(newValue, copiedValidValues)) {
                    AppLogger.error("InputChoiceProperty: Invalid value set: " + newValue +
                            ". Valid values are: " + copiedValidValues);
                }
                // The value is set even if it is invalid.
                super.set(newValue);
            }
        };
        return new InputChoiceProperty<>(property, copiedValidValues, displayNameProvider);
    }

    /**
     * Checks if a value is invalid for the given list of valid values.
     *
     * @param value       the value to check
     * @param validValues the list of valid values
     * @param <T>         the value type
     * @return {@code true} if the value is invalid, {@code false} otherwise
     */
    static <T> boolean isInvalidValue(T value, List<T> validValues) {
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
    public T getValue() {
        return property.get();
    }

    /**
     * Sets the property value.
     *
     * @param value the value to set
     */
    public void setValue(T value) {
        property.set(value);
    }

    /**
     * Checks if the current value of the property is equal to the specified value.
     *
     * @param value the value to compare with the current property value
     * @return {@code true} if the current value equals the specified value, {@code false} otherwise
     */
    public boolean isValue(T value) {
        return Objects.equals(getValue(), value);
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
