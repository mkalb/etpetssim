package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;

import java.util.*;

/**
 * An implementation of {@link GridModel} that stores only non-default entities in a map.
 * Efficient for sparse grids where most cells contain the default entity.
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public final class SparseGridModel<T extends GridEntity> implements GridModel<T> {

    /** The structure describing the grid's dimensions and valid coordinates. */
    private final GridStructure structure;

    /** The default entity for all grid cells. */
    private final T defaultEntity;

    /** The map holding non-default grid entities, keyed by coordinate. */
    private final Map<GridCoordinate, T> data;

    /**
     * Constructs a new {@code SparseGridModel} with the given structure and default entity.
     * Initially, all cells are set to the default entity (i.e., the map is empty).
     *
     * @param structure the grid structure (must not be null)
     * @param defaultEntity the default entity for all cells (must not be null)
     */
    public SparseGridModel(GridStructure structure, T defaultEntity) {
        Objects.requireNonNull(structure);
        Objects.requireNonNull(defaultEntity);
        this.structure = Objects.requireNonNull(structure);
        this.defaultEntity = Objects.requireNonNull(defaultEntity);
        data = new HashMap<>();
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
     * Creates a deep copy of this grid model, including all non-default entities.
     *
     * @return a new {@code SparseGridModel} with the same structure, default entity, and data
     */
    @Override
    public SparseGridModel<T> copy() {
        SparseGridModel<T> clone = new SparseGridModel<>(structure, defaultEntity);
        clone.data.putAll(data);
        return clone;
    }

    /**
     * Creates a new {@code SparseGridModel} with the same structure and default entity,
     * but with all cells set to the default entity (i.e., empty map).
     *
     * @return a blank copy of this grid model
     */
    @Override
    public SparseGridModel<T> copyWithDefaultEntity() {
        return new SparseGridModel<>(structure, defaultEntity);
    }

    /**
     * Returns the entity at the specified coordinate.
     * If the coordinate is not present in the map, returns the default entity.
     *
     * @param coordinate the grid coordinate (must not be null)
     * @return the entity at the given coordinate, or the default entity if not set
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    public T getEntity(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return data.getOrDefault(coordinate, defaultEntity);
    }

    /**
     * Sets the entity at the specified coordinate.
     * If the entity equals the default entity, the entry is removed from the map.
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
        if (entity.equals(defaultEntity)) {
            data.remove(coordinate);
        } else {
            data.put(coordinate, entity);
        }
    }

    /**
     * Sets all grid cells to the default entity (i.e., clears the map).
     */
    @Override
    public void clear() {
        data.clear();
    }

    /**
     * Sets all grid cells to the specified entity.
     *
     * @param entity the entity to set
     */
    @Override
    public void fill(T entity) {
        Objects.requireNonNull(entity);
        data.clear(); // Clear existing entries
        if (!entity.equals(defaultEntity)) { // Set new entities only if different from default
            structure.coordinatesStream().parallel().forEach(coordinate -> data.put(coordinate, entity));
        }
    }

    /**
     * Indicates that this grid model is sparse.
     *
     * @return {@code true}, as this implementation is for sparse grids
     */
    @Override
    public boolean isModelSparse() {
        return true;
    }

    /**
     * Returns a string representation of this grid model, including its structure and default entity.
     *
     * @return a string representation of this grid model
     */
    @Override
    public String toString() {
        return "SparseGridModel{" +
                "structure=" + structure +
                ", defaultEntity=" + defaultEntity +
                ", data.size()=" + data.size() +
                '}';
    }

}