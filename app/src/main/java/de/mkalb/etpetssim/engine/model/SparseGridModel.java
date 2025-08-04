package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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
     * @param structure the grid structure
     * @param defaultEntity the default entity for all cells
     */
    public SparseGridModel(GridStructure structure, T defaultEntity) {
        this.structure = structure;
        this.defaultEntity = defaultEntity;
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
     * @param coordinate the grid coordinate
     * @return the entity at the given coordinate, or the default entity if not set
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    public T getEntity(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return data.getOrDefault(coordinate, defaultEntity);
    }

    /**
     * Determines if the entity at the specified coordinate is the default entity.
     * <p>
     * This method checks whether the given coordinate is valid within the grid structure
     * and whether the entity at that coordinate is the default entity. If the coordinate
     * is not present in the internal data map, it is considered to hold the default entity.
     * </p>
     *
     * @param coordinate the grid coordinate to check
     * @return {@code true} if the entity at the coordinate is the default entity, {@code false} otherwise
     * @throws IndexOutOfBoundsException if the coordinate is not valid within the grid structure
     */
    @Override
    public boolean isDefaultEntity(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return !data.containsKey(coordinate);
    }

    /**
     * Sets the entity at the specified coordinate.
     * If the entity equals the default entity, the entry is removed from the map.
     *
     * @param coordinate the grid coordinate
     * @param entity the entity to set
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    public void setEntity(GridCoordinate coordinate, T entity) {
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
        data.clear(); // Clear existing entries
        if (!entity.equals(defaultEntity)) { // Set new entities only if different from default
            structure.coordinatesStream().forEach(coordinate -> data.put(coordinate, entity));
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
     * Swaps the entities at the coordinates of the two given {@link GridCell} objects.
     * <p>
     * After execution, the entity from {@code cellA} will be at {@code cellB}'s coordinate,
     * and the entity from {@code cellB} will be at {@code cellA}'s coordinate.
     * </p>
     *
     * @param cellA the first grid cell whose entity and coordinate are involved in the swap
     * @param cellB the second grid cell whose entity and coordinate are involved in the swap
     * @throws IndexOutOfBoundsException if either coordinate is not valid in this grid
     */
    @Override
    public void swapEntities(GridCell<T> cellA, GridCell<T> cellB) {
        GridCoordinate coordinateA = cellA.coordinate();
        GridCoordinate coordinateB = cellB.coordinate();
        if (!structure.isCoordinateValid(coordinateA)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinateA + " for structure: " + structure);
        }
        if (!structure.isCoordinateValid(coordinateB)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinateB + " for structure: " + structure);
        }
        T entityA = data.getOrDefault(coordinateA, defaultEntity);
        T entityB = data.getOrDefault(coordinateB, defaultEntity);

        if (entityB.equals(defaultEntity)) {
            data.remove(coordinateA);
        } else {
            data.put(coordinateA, entityB);
        }

        if (entityA.equals(defaultEntity)) {
            data.remove(coordinateB);
        } else {
            data.put(coordinateB, entityA);
        }
    }

    @Override
    public Stream<GridCell<T>> nonDefaultCells() {
        return data.entrySet().stream()
                   .map(entry -> new GridCell<>(entry.getKey(), entry.getValue()));
    }

    @Override
    public List<GridCell<T>> filteredAndSortedCells(Predicate<T> entityPredicate, Comparator<GridCell<T>> cellOrdering) {
        List<GridCell<T>> result = new ArrayList<>();
        for (Map.Entry<GridCoordinate, T> entry : data.entrySet()) {
            T entity = entry.getValue();
            if (entityPredicate.test(entity)) {
                result.add(new GridCell<>(entry.getKey(), entity));
            }
        }
        result.sort(cellOrdering);
        return result;
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