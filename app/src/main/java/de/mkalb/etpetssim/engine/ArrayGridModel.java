package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * An implementation of {@link GridModel} that stores grid values in a two-dimensional array.
 * Efficient for dense grids with mostly non-default values.
 *
 * @param <T> the type of values stored in the grid
 */
public final class ArrayGridModel<T> implements GridModel<T> {

    /** The structure describing the grid's dimensions and valid coordinates. */
    private final GridStructure structure;

    /** The default value for all grid cells. */
    private final T defaultValue;

    /** The two-dimensional array holding the grid values. */
    private final Object[][] data;

    /**
     * Constructs a new {@code ArrayGridModel} with the given structure and default value.
     * All cells are initialized to the default value.
     *
     * @param structure the grid structure (must not be null)
     * @param defaultValue the default value for all cells (must not be null)
     * @throws NullPointerException if structure or defaultValue is null
     */
    public ArrayGridModel(GridStructure structure, T defaultValue) {
        Objects.requireNonNull(structure);
        Objects.requireNonNull(defaultValue);
        this.structure = Objects.requireNonNull(structure);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        data = new Object[structure.size().height()][structure.size().width()];
        clear();
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
     * Creates a deep copy of this grid model, including all values.
     *
     * @return a new {@code ArrayGridModel} with the same structure, default value, and data
     */
    @Override
    public ArrayGridModel<T> copy() {
        ArrayGridModel<T> clone = new ArrayGridModel<>(structure, defaultValue);
        for (int y = 0; y < structure.size().height(); y++) {
            System.arraycopy(data[y], 0, clone.data[y], 0, structure.size().width());
        }
        return clone;
    }

    /**
     * Creates a new {@code ArrayGridModel} with the same structure and default value,
     * but with all cells set to the default value.
     *
     * @return a blank copy of this grid model
     */
    @Override
    public ArrayGridModel<T> copyWithDefaultValues() {
        return new ArrayGridModel<>(structure, defaultValue);
    }

    /**
     * Returns the value at the specified coordinate.
     *
     * @param coordinate the grid coordinate (must not be null)
     * @return the value at the given coordinate
     * @throws NullPointerException if coordinate is null
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getValue(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return (T) data[coordinate.y()][coordinate.x()];
    }

    /**
     * Sets the value at the specified coordinate.
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
        data[coordinate.y()][coordinate.x()] = Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Sets all grid cells to the default value.
     */
    @Override
    public void clear() {
        for (int y = 0; y < structure.size().height(); y++) {
            Arrays.fill(data[y], defaultValue);
        }
    }

    /**
     * Sets all grid cells to the specified value.
     *
     * @param value the value to set
     */
    @Override
    public void fill(T value) {
        Objects.requireNonNull(value);
        for (Object[] row : data) {
            Arrays.fill(row, value);
        }
    }

    /**
     * Indicates that this grid model is not sparse.
     *
     * @return {@code false}, as this implementation is for dense grids
     */
    @Override
    public boolean isSparse() {
        return false;
    }

    /**
     * Returns a string representation of this grid model, including its structure and default value.
     *
     * @return a string representation of this grid model
     */
    @Override
    public String toString() {
        return "ArrayGridModel{" +
                "structure=" + structure +
                ", defaultValue=" + defaultValue +
                ", data.length=" + data.length +
                '}';
    }

}
