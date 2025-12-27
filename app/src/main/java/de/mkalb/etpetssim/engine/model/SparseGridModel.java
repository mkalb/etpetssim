package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * An implementation of {@link WritableGridModel} that stores only non-default entities in a map.
 * Efficient for sparse grids where most cells contain the default entity.
 *
 * @param <T> the type of entities stored in the grid, must implement {@link de.mkalb.etpetssim.engine.model.entity.GridEntity}
 */
public final class SparseGridModel<T extends GridEntity> implements WritableGridModel<T> {

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

    @Override
    public T getEntity(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return data.getOrDefault(coordinate, defaultEntity);
    }

    @Override
    public boolean isDefaultEntity(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return !data.containsKey(coordinate);
    }

    @Override
    public boolean isModelSparse() {
        return true;
    }

    @Override
    public Stream<GridCell<T>> nonDefaultCells() {
        return data.entrySet().stream()
                   .map(entry -> new GridCell<>(entry.getKey(), entry.getValue()));
    }

    @Override
    public Set<GridCoordinate> nonDefaultCoordinates() {
        return Collections.unmodifiableSet(data.keySet());
    }

    @Override
    public long countCells(Predicate<? super GridCell<T>> predicate) {
        long count = 0;
        for (GridCoordinate coordinate : structure.coordinatesList()) {
            T entity = getEntity(coordinate);
            GridCell<T> cell = new GridCell<>(coordinate, entity);
            if (predicate.test(cell)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public long countEntities(Predicate<? super T> predicate) {
        long count = 0;
        for (GridCoordinate coordinate : structure.coordinatesList()) {
            T entity = getEntity(coordinate);
            if (predicate.test(entity)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<GridCoordinate> filteredCoordinates(Predicate<T> entityPredicate) {
        boolean includeDefault = entityPredicate.test(defaultEntity);

        // Fast path: default does NOT match -> only stored non-default entries can match.
        if (!includeDefault) {
            if (data.isEmpty()) {
                return List.of();
            }
            List<GridCoordinate> result = new ArrayList<>(data.size());
            for (Map.Entry<GridCoordinate, T> entry : data.entrySet()) {
                T entity = entry.getValue();
                if (entityPredicate.test(entity)) {
                    result.add(entry.getKey());
                }
            }
            return result;
        }

        // Default matches: scan full grid; default cells are included without extra predicate tests.
        List<GridCoordinate> result = new ArrayList<>(structure.size().area());
        for (GridCoordinate coordinate : structure.coordinatesList()) {
            T entity = data.get(coordinate);
            if (entity == null) {
                result.add(coordinate);
            } else if (entityPredicate.test(entity)) {
                result.add(coordinate);
            }
        }
        return result;
    }

    @Override
    public List<GridCell<T>> filteredCells(Predicate<T> entityPredicate) {
        boolean includeDefault = entityPredicate.test(defaultEntity);

        // Fast path: default does NOT match -> only stored non-default entries can match.
        if (!includeDefault) {
            if (data.isEmpty()) {
                return List.of();
            }
            List<GridCell<T>> result = new ArrayList<>(data.size());
            for (Map.Entry<GridCoordinate, T> entry : data.entrySet()) {
                T entity = entry.getValue();
                if (entityPredicate.test(entity)) {
                    result.add(new GridCell<>(entry.getKey(), entity));
                }
            }
            return result;
        }

        // Default matches: all coordinates NOT present in data are included without further checks.
        List<GridCell<T>> result = new ArrayList<>(structure.size().area());
        for (GridCoordinate coordinate : structure.coordinatesList()) {
            T entity = data.get(coordinate);
            if (entity == null) {
                result.add(new GridCell<>(coordinate, defaultEntity));
            } else {
                if (entityPredicate.test(entity)) {
                    result.add(new GridCell<>(coordinate, entity));
                }
            }
        }

        return result;
    }

    @Override
    public List<GridCell<T>> filteredAndSortedCells(Predicate<T> entityPredicate, Comparator<GridCell<T>> cellOrdering) {
        List<GridCell<T>> result = filteredCells(entityPredicate);
        result.sort(cellOrdering);
        return result;
    }

    @Override
    public SparseGridModel<T> copy() {
        SparseGridModel<T> clone = new SparseGridModel<>(structure, defaultEntity);
        clone.data.putAll(data);
        return clone;
    }

    @Override
    public SparseGridModel<T> copyWithDefaultEntity() {
        return new SparseGridModel<>(structure, defaultEntity);
    }

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

    @Override
    public void setEntityToDefault(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        data.remove(coordinate);
    }

    @Override
    public void fill(T entity) {
        data.clear(); // Clear existing entities.
        if (!entity.equals(defaultEntity)) { // Set new entities only if different from the default.
            structure.coordinatesStream().forEach(coordinate -> data.put(coordinate, entity));
        }
    }

    @Override
    public void fill(Supplier<T> supplier) {
        data.clear();
        for (GridCoordinate coordinate : structure.coordinatesList()) {
            T entity = supplier.get();
            if (!entity.equals(defaultEntity)) {
                data.put(coordinate, entity);
            }
        }
    }

    @Override
    public void fill(Function<GridCoordinate, T> mapper) {
        data.clear();
        for (GridCoordinate coordinate : structure.coordinatesList()) {
            T entity = mapper.apply(coordinate);
            if (!entity.equals(defaultEntity)) {
                data.put(coordinate, entity);
            }
        }
    }

    @Override
    public void clear() {
        data.clear();
    }

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
    public String toString() {
        return "SparseGridModel{" +
                "structure=" + structure +
                ", defaultEntity=" + defaultEntity +
                ", data.size()=" + data.size() +
                '}';
    }

}
