package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * An implementation of {@link GridModel} that stores only non-default values in a map.
 * Efficient for sparse grids where most cells contain the default value.
 *
 * @param <T> the type of values stored in the grid
 */
public final class SparseGridModel<T> implements GridModel<T> {

    /** The structure describing the grid's dimensions and valid coordinates. */
    private final GridStructure structure;

    /** The default value for all grid cells. */
    private final T defaultValue;

    /** The map holding non-default grid values, keyed by coordinate. */
    private final Map<GridCoordinate, T> data;

    /**
     * Constructs a new {@code SparseGridModel} with the given structure and default value.
     * Initially, all cells are set to the default value (i.e., the map is empty).
     *
     * @param structure the grid structure (must not be null)
     * @param defaultValue the default value for all cells (must not be null)
     * @throws NullPointerException if structure or defaultValue is null
     */
    public SparseGridModel(GridStructure structure, T defaultValue) {
        Objects.requireNonNull(structure);
        Objects.requireNonNull(defaultValue);
        this.structure = Objects.requireNonNull(structure);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        data = new HashMap<>();
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public T defaultValue() {
        return defaultValue;
    }

    /**
     * Creates a deep copy of this grid model, including all non-default values.
     *
     * @return a new {@code SparseGridModel} with the same structure, default value, and data
     */
    @Override
    public SparseGridModel<T> copy() {
        SparseGridModel<T> clone = new SparseGridModel<>(structure, defaultValue);
        clone.data.putAll(data);
        return clone;
    }

    /**
     * Creates a new {@code SparseGridModel} with the same structure and default value,
     * but with all cells set to the default value (i.e., empty map).
     *
     * @return a blank copy of this grid model
     */
    @Override
    public SparseGridModel<T> copyWithDefaultValues() {
        return new SparseGridModel<>(structure, defaultValue);
    }

    /**
     * Returns the value at the specified coordinate.
     * If the coordinate is not present in the map, returns the default value.
     *
     * @param coordinate the grid coordinate (must not be null)
     * @return the value at the given coordinate, or the default value if not set
     * @throws NullPointerException if coordinate is null
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    public T getValue(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return data.getOrDefault(coordinate, defaultValue);
    }

    /**
     * Sets the value at the specified coordinate.
     * If the value equals the default value, the entry is removed from the map.
     *
     * @param coordinate the grid coordinate (must not be null)
     * @param value the value to set (must not be null)
     * @throws NullPointerException if coordinate or value is null
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    public void setValue(GridCoordinate coordinate, T value) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(value);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        if (Objects.equals(value, defaultValue)) {
            data.remove(coordinate);
        } else {
            data.put(coordinate, value);
        }
    }

    /**
     * Sets all grid cells to the default value (i.e., clears the map).
     */
    @Override
    public void clear() {
        data.clear();
    }

    /**
     * Indicates that this grid model is sparse.
     *
     * @return {@code true}, as this implementation is for sparse grids
     */
    @Override
    public boolean isSparse() {
        return true;
    }

    /**
     * Returns a string representation of this grid model, including its structure and default value.
     *
     * @return a string representation of this grid model
     */
    @Override
    public String toString() {
        return "SparseGridModel{" +
                "structure=" + structure +
                ", defaultValue=" + defaultValue +
                ", data.size()=" + data.size() +
                '}';
    }

}
