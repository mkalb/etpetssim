package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a property with a defined set of valid values and a display name provider.
 *
 * <p>Encapsulates an {@link ObjectProperty} for a single selected value and provides
 * validation against a specified list of valid values.
 *
 * <p>Invalid values are logged via {@link AppLogger} and still set on the property.
 *
 * <p>Use {@link #ofList(Object, List, Function)} or {@link #ofEnum(Enum, Class, Function)} to create instances.
 *
 * <p>The record wraps the given property without copying it; bindings and listeners on the property stay effective.
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
     * Creates an input property with an unmodifiable copy of {@code validValues}.
     *
     * @throws IllegalArgumentException if {@code validValues} is empty or contains duplicates, or if the value of
     *                                  {@code property} is not one of {@code validValues}
     */
    public InputChoiceProperty {
        if (validValues.isEmpty()) {
            throw new IllegalArgumentException("validValues must not be empty");
        }
        if (new HashSet<>(validValues).size() != validValues.size()) {
            throw new IllegalArgumentException("validValues must not contain duplicates");
        }
        if (isInvalidValue(property.get(), validValues)) {
            throw new IllegalArgumentException("Initial value is not valid: " + property.get());
        }
        validValues = List.copyOf(validValues);
    }

    /**
     * Creates a new {@code InputChoiceProperty} with the specified initial value, valid values, and display name
     * provider.
     *
     * <p>Validation is performed by the canonical constructor.
     *
     * @param initialValue        the initial selected value
     * @param validValues         the list of valid values
     * @param displayNameProvider a function mapping values to their display names
     * @param <T>                 the value type
     * @return a new {@code InputChoiceProperty}
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static <T> InputChoiceProperty<T> ofList(T initialValue,
                                                    List<T> validValues,
                                                    Function<T, String> displayNameProvider) {
        List<T> copiedValidValues = List.copyOf(validValues);
        var property = new SimpleObjectProperty<>(initialValue) {
            @Override
            public void set(T newValue) {
                if (isInvalidValue(newValue, copiedValidValues)) {
                    AppLogger.errorf("InputChoiceProperty: Invalid value set: %s. Valid values are: %s",
                            newValue, copiedValidValues);
                }
                // The value is set even if it is invalid.
                super.set(newValue);
            }
        };
        return new InputChoiceProperty<>(property, copiedValidValues, displayNameProvider);
    }

    /**
     * Creates a new {@code InputChoiceProperty} with all values of the given enum type and a display name provider.
     *
     * <p>Uses {@code enumClass.getEnumConstants()} to determine the valid values.
     *
     * @param initialValue        the initial selected enum value
     * @param enumClass           the {@link Class} object of the enum type
     * @param displayNameProvider a function mapping values to their display names
     * @param <T>                 the enum type
     * @return a new {@code InputChoiceProperty} with all enum values as valid values
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static <T extends Enum<T>> InputChoiceProperty<T> ofEnum(T initialValue,
                                                                    Class<T> enumClass,
                                                                    Function<T, String> displayNameProvider) {
        return ofList(initialValue, List.of(enumClass.getEnumConstants()), displayNameProvider);
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
        return Bindings.createStringBinding(() -> format.formatted(displayNameProvider.apply(property.get())),
                property);
    }

    public T getValue() {
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
