package de.mkalb.etpetssim.engine;

import java.util.function.*;

/**
 * Represents a generic grid model that stores values at grid coordinates.
 * Provides methods for accessing, modifying, and querying grid data.
 *
 * @param <T> the type of values stored in the grid
 */
public interface GridModel<T> extends ReadableGridModel<T> {

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
     * Sets the value at the specified coordinate to the default value.
     *
     * @param coordinate the grid coordinate
     */
    default void setValueToDefault(GridCoordinate coordinate) {
        setValue(coordinate, defaultValue());
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

}
