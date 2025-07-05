package de.mkalb.etpetssim.engine;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Represents a generic grid model that stores values at grid coordinates.
 * Provides methods for accessing, modifying, and querying grid data.
 *
 * @param <T> the type of values stored in the grid
 */
public interface GridModel<T> {

    /**
     * Returns the structure of the grid, including its dimensions and valid coordinates.
     *
     * @return the grid structure
     */
    GridStructure structure();

    /**
     * Returns the default value for grid cells.
     *
     * @return the default value
     */
    T defaultValue();

    /**
     * Creates a deep copy of this grid model, including all values.
     *
     * @return a copy of this grid model
     */
    GridModel<T> copy();

    /**
     * Creates a copy of this grid model with all values set to the default value.
     *
     * @return a blank copy of this grid model
     */
    GridModel<T> copyWithDefaultValues();

    /**
     * Returns the value at the specified coordinate.
     *
     * @param coordinate the grid coordinate
     * @return the value at the coordinate
     */
    T getValue(GridCoordinate coordinate);

    /**
     * Sets the value at the specified coordinate.
     *
     * @param coordinate the grid coordinate
     * @param value the value to set
     */
    void setValue(GridCoordinate coordinate, T value);

    /**
     * Sets all grid cells to the default value.
     */
    default void clear() {
        fill(defaultValue());
    }

    /**
     * Indicates whether this grid model is sparse (optimized for mostly default values).
     *
     * @return true if the grid is sparse, false otherwise
     */
    boolean isSparse();

    /**
     * Sets the value at the specified coordinate to the default value.
     *
     * @param coordinate the grid coordinate
     */
    default void setValueToDefault(GridCoordinate coordinate) {
        setValue(coordinate, defaultValue());
    }

    /**
     * Checks if the given coordinate is valid within the grid structure.
     *
     * @param coordinate the grid coordinate
     * @return true if the coordinate is valid, false otherwise
     */
    default boolean isCoordinateValid(GridCoordinate coordinate) {
        return structure().isCoordinateValid(coordinate);
    }

    /**
     * Returns the value at the specified coordinate as an {@link Optional}.
     * Returns {@code Optional.empty()} if the coordinate is invalid.
     *
     * @param coordinate the grid coordinate
     * @return an Optional containing the value, or empty if invalid
     */
    default Optional<T> getValueAsOptional(GridCoordinate coordinate) {
        if (isCoordinateValid(coordinate)) {
            return Optional.of(getValue(coordinate));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Sets all grid cells to the specified value.
     *
     * @param value the value to set
     */
    default void fill(T value) {
        structure().coordinatesStream().forEachOrdered(coordinate -> setValue(coordinate, value));
    }

    /**
     * Sets all grid cells using a mapping function from coordinate to value.
     *
     * @param mapper the function to compute values for each coordinate
     */
    default void fill(Function<GridCoordinate, T> mapper) {
        structure().coordinatesStream().forEachOrdered(coordinate -> setValue(coordinate, mapper.apply(coordinate)));
    }

    /**
     * Returns a map of coordinates to values for which the predicate returns true.
     *
     * @param isValid predicate to filter values
     * @return a map of coordinates to values
     */
    default Map<GridCoordinate, T> toMap(Predicate<T> isValid) {
        Map<GridCoordinate, T> result = new LinkedHashMap<>();
        for (GridCoordinate coordinate : structure().coordinatesList()) {
            T value = getValue(coordinate);
            if (isValid.test(value)) {
                result.put(coordinate, value);
            }
        }
        return result;
    }

    /**
     * Returns a map of coordinates to values that are not equal to the default value.
     *
     * @return a map of coordinates to non-default values
     */
    default Map<GridCoordinate, T> toMap() {
        return toMap(value -> !Objects.equals(value, defaultValue()));
    }

    /**
     * Returns a stream of all values in the grid.
     *
     * @return a stream of grid values
     */
    default Stream<T> stream() {
        return structure().coordinatesStream().map(this::getValue);
    }

    /**
     * Checks if all grid cells contain the default value.
     *
     * @return true if the grid is empty, false otherwise
     */
    default boolean isEmpty() {
        return stream().allMatch(value -> Objects.equals(value, defaultValue()));
    }

}
