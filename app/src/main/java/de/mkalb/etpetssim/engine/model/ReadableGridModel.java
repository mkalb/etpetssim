package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Read-only view of a {@link WritableGridModel}.
 * <p>
 * Provides methods for accessing grid entities and querying grid state.
 * All mutating (write and update) methods are defined in the {@link WritableGridModel} interface.
 * </p>
 *
 * @param <T> the type of entities stored in the grid, must implement {@link de.mkalb.etpetssim.engine.model.entity.GridEntity}
 */
public sealed interface ReadableGridModel<T extends GridEntity> extends GridModel<T>
        permits WritableGridModel {

    @Override
    default boolean isComposite() {
        return false;
    }

    /**
     * Returns the default entity for grid cells.
     *
     * @return the default entity
     */
    T defaultEntity();

    /**
     * Returns the entity at the specified coordinate.
     *
     * @param coordinate the grid coordinate
     * @return the entity at the coordinate
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    T getEntity(GridCoordinate coordinate);

    /**
     * Returns a {@link GridCell} containing the specified coordinate and its associated entity.
     *
     * @param coordinate the grid coordinate
     * @return a GridCell with the coordinate and its entity
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    default GridCell<T> getGridCell(GridCoordinate coordinate) {
        return new GridCell<>(coordinate, getEntity(coordinate));
    }

    /**
     * Checks whether the entity at the specified coordinate is equal to the default entity.
     *
     * @param coordinate the grid coordinate to check
     * @return {@code true} if the entity at the coordinate is the default entity, {@code false} otherwise
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    default boolean isDefaultEntity(GridCoordinate coordinate) {
        return getEntity(coordinate).equals(defaultEntity());
    }

    /**
     * Indicates whether this grid model is sparse (optimized for mostly default entities).
     *
     * @return true if the grid is sparse, false otherwise
     */
    boolean isModelSparse();

    /**
     * Returns the entity at the specified coordinate as an {@link Optional}.
     * Returns {@code Optional.empty()} if the coordinate is invalid.
     *
     * @param coordinate the grid coordinate
     * @return an Optional containing the entity, or empty if invalid
     */
    default Optional<T> getEntityAsOptional(GridCoordinate coordinate) {
        if (isCoordinateValid(coordinate)) {
            return Optional.of(getEntity(coordinate));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns a stream of all grid cells (coordinate and entity).
     *
     * @return a stream of GridCell
     */
    default Stream<GridCell<T>> cells() {
        return structure().coordinatesStream()
                          .map(coordinate -> new GridCell<>(coordinate, getEntity(coordinate)));
    }

    /**
     * Returns a stream of grid cells whose entity is not the default entity.
     *
     * @return a stream of non-default GridCell
     */
    default Stream<GridCell<T>> nonDefaultCells() {
        T def = defaultEntity();
        return cells().filter(cell -> !Objects.equals(cell.entity(), def));
    }

    /**
     * Returns a set of coordinates for all grid cells whose entity is not equal to the default entity.
     *
     * @return an unmodifiable set of coordinates for non-default entities
     */
    default Set<GridCoordinate> nonDefaultCoordinates() {
        return nonDefaultCells()
                .map(GridCell::coordinate)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Counts the number of grid cells that match the given predicate.
     * <p>
     * This method filters the stream of all grid cells using the provided predicate
     * and returns the count of matching cells.
     *
     * @param predicate the condition to test each grid cell against
     * @return the count of grid cells that match the predicate
     */
    default long countCells(Predicate<? super GridCell<T>> predicate) {
        return cells().filter(predicate).count();
    }

    /**
     * Counts the number of entities that match the given predicate.
     * <p>
     * This method filters the stream of all entities using the provided predicate
     * and returns the count of matching entities.
     *
     * @param predicate the condition to test each entity against
     * @return the count of entities that match the predicate
     */
    default long countEntities(Predicate<? super T> predicate) {
        return structure().coordinatesStream()
                          .map(this::getEntity)
                          .filter(predicate)
                          .count();
    }

    /**
     * Returns a list of grid cells whose entities match the given predicate,
     * ordered according to the provided comparator.
     * <p>
     * This method filters all grid cells using the specified {@code entityPredicate}
     * and sorts the resulting cells using {@code cellOrdering}.
     *
     * @param entityPredicate the predicate to filter grid cell entities
     * @param cellOrdering the comparator to define the order of the resulting grid cells
     * @return a list of filtered and sorted {@link GridCell} objects
     */
    default List<GridCell<T>> filteredAndSortedCells(Predicate<T> entityPredicate, Comparator<GridCell<T>> cellOrdering) {
        return cells()
                .filter(cell -> entityPredicate.test(cell.entity()))
                .sorted(cellOrdering)
                .toList();
    }

}
