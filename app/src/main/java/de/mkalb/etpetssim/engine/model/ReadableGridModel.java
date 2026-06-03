package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;
import java.util.function.*;

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
    boolean isDefaultEntity(GridCoordinate coordinate);

    /**
     * Indicates whether this grid model is sparse (optimized for mostly default entities).
     *
     * @return true if the grid is sparse, false otherwise
     */
    boolean isSparse();

    /**
     * Counts the number of entities that match the given predicate.
     *
     * @param predicate the condition to test each entity against
     * @return the count of entities that match the predicate
     */
    long countEntities(Predicate<? super T> predicate);

    /**
     * Returns a mutable set of all coordinates at which the entity is not
     * the default entity.
     * <p>
     * The set is a snapshot of the model state taken at the time of this call
     * and is not affected by subsequent mutations to the model.
     * Iteration order is implementation-defined.
     *
     * @return a mutable set of coordinates with non-default entities
     */
    Set<GridCoordinate> nonDefaultCoordinates();

    /**
     * Returns a list of grid coordinates whose entities match the given predicate.
     * Avoids creating intermediate {@link GridCell} instances compared to {@link #filteredCells}.
     * <p>
     * The list is a snapshot of the model state taken at the time of this call
     * and is not affected by subsequent mutations to the model.
     * Iteration order is implementation-defined.
     *
     * @param entityPredicate the predicate to filter entities
     * @return a list of matching coordinates
     */
    List<GridCoordinate> filteredCoordinates(Predicate<T> entityPredicate);

    /**
     * Selects a random coordinate from the grid that contains the default entity.
     *
     * @param random the random number generator to use
     * @return an Optional containing a random default coordinate, or empty if none exist
     */
    Optional<GridCoordinate> findRandomDefaultCoordinate(Random random);

    /**
     * Returns a mutable list of all grid cells, in row-major order
     * (x-coordinate varies fastest within each row, y-coordinate varies slowest).
     * <p>
     * The list is a snapshot of the model state taken at the time of this call
     * and is not affected by subsequent mutations to the model.
     *
     * @return a mutable list of all {@link GridCell} instances in row-major order
     */
    List<GridCell<T>> allCells();

    /**
     * Returns a mutable list of all grid cells whose entity is not the default entity.
     * <p>
     * The list is a snapshot of the model state taken at the time of this call
     * and is not affected by subsequent mutations to the model.
     * Iteration order is implementation-defined.
     *
     * @return a mutable list of {@link GridCell} instances with non-default entities
     */
    List<GridCell<T>> nonDefaultCells();

    /**
     * Returns a mutable list of grid cells whose entities match the given predicate.
     * <p>
     * The list is a snapshot of the model state taken at the time of this call
     * and is not affected by subsequent mutations to the model.
     * Iteration order is implementation-defined.
     *
     * @param entityPredicate the predicate to filter grid cell entities
     * @return a mutable list of filtered {@link GridCell} instances
     */
    List<GridCell<T>> filteredCells(Predicate<T> entityPredicate);

    /**
     * Returns a mutable list of grid cells whose entities match the given predicate,
     * ordered according to the provided comparator.
     * <p>
     * The list is a snapshot of the model state taken at the time of this call
     * and is not affected by subsequent mutations to the model.
     *
     * @param entityPredicate the predicate to filter grid cell entities
     * @param cellOrdering the comparator to define the order of the resulting grid cells
     * @return a mutable list of filtered and sorted {@link GridCell} instances
     */
    List<GridCell<T>> filteredCellsSortedBy(Predicate<T> entityPredicate, Comparator<GridCell<T>> cellOrdering);

}
