package de.mkalb.etpetssim.engine;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Read-only view of a grid model.
 *
 * @param <T> the type of values stored in the grid
 */
public interface ReadableGridModel<T> {

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
     * Returns the value at the specified coordinate.
     *
     * @param coordinate the grid coordinate
     * @return the value at the coordinate
     */
    T getValue(GridCoordinate coordinate);

    /**
     * Indicates whether this grid model is sparse (optimized for mostly default values).
     *
     * @return true if the grid is sparse, false otherwise
     */
    boolean isSparse();

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
