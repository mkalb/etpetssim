package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;

import java.util.*;

/**
 * An implementation of {@link GridModel} that stores grid entities in a two-dimensional array.
 * Efficient for dense grids with mostly non-default entities.
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public final class ArrayGridModel<T extends GridEntity> implements GridModel<T> {

    /** The structure describing the grid's dimensions and valid coordinates. */
    private final GridStructure structure;

    /** The default entity for all grid cells. */
    private final T defaultEntity;

    /** The two-dimensional array holding the grid entities. */
    private final Object[][] data;

    /**
     * Constructs a new {@code ArrayGridModel} with the given structure and default entity.
     * All cells are initialized to the default entity.
     *
     * @param structure the grid structure (must not be null)
     * @param defaultEntity the default entity for all cells (must not be null)
     */
    public ArrayGridModel(GridStructure structure, T defaultEntity) {
        Objects.requireNonNull(structure);
        Objects.requireNonNull(defaultEntity);
        this.structure = Objects.requireNonNull(structure);
        this.defaultEntity = Objects.requireNonNull(defaultEntity);
        data = new Object[structure.size().height()][structure.size().width()];
        clear();
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public T defaultEntity() {
        return defaultEntity;
    }

    /**
     * Creates a deep copy of this grid model, including all entities.
     *
     * @return a new {@code ArrayGridModel} with the same structure, default entity, and data
     */
    @Override
    public ArrayGridModel<T> copy() {
        ArrayGridModel<T> clone = new ArrayGridModel<>(structure, defaultEntity);
        for (int y = 0; y < structure.size().height(); y++) {
            System.arraycopy(data[y], 0, clone.data[y], 0, structure.size().width());
        }
        return clone;
    }

    /**
     * Creates a new {@code ArrayGridModel} with the same structure and default entity,
     * but with all cells set to the default entity.
     *
     * @return a blank copy of this grid model
     */
    @Override
    public ArrayGridModel<T> copyWithDefaultEntity() {
        return new ArrayGridModel<>(structure, defaultEntity);
    }

    /**
     * Returns the entity at the specified coordinate.
     *
     * @param coordinate the grid coordinate (must not be null)
     * @return the entity at the given coordinate
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getEntity(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return (T) data[coordinate.y()][coordinate.x()];
    }

    /**
     * Sets the entity at the specified coordinate.
     *
     * @param coordinate the grid coordinate (must not be null)
     * @param entity the entity to set (must not be null)
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    public void setEntity(GridCoordinate coordinate, T entity) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(entity);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        data[coordinate.y()][coordinate.x()] = entity;
    }

    /**
     * Sets all grid cells to the default entity.
     */
    @Override
    public void clear() {
        for (int y = 0; y < structure.size().height(); y++) {
            Arrays.fill(data[y], defaultEntity);
        }
    }

    /**
     * Sets all grid cells to the specified entity.
     *
     * @param entity the entity to set
     */
    @Override
    public void fill(T entity) {
        Objects.requireNonNull(entity);
        for (Object[] row : data) {
            Arrays.fill(row, entity);
        }
    }

    /**
     * Indicates that this grid model is not sparse.
     *
     * @return {@code false}, as this implementation is for dense grids
     */
    @Override
    public boolean isModelSparse() {
        return false;
    }

    /**
     * Returns a string representation of this grid model, including its structure and default entity.
     *
     * @return a string representation of this grid model
     */
    @Override
    public String toString() {
        return "ArrayGridModel{" +
                "structure=" + structure +
                ", defaultEntity=" + defaultEntity +
                ", data.length=" + data.length +
                '}';
    }

}
