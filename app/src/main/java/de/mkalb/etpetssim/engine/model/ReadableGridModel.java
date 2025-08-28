package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Read-only view of a {@link GridModel}.
 * <p>
 * All mutating (write and update) methods are defined in the {@link GridModel} interface.
 * This interface provides only read-only access to grid entities.
 * </p>
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public interface ReadableGridModel<T extends GridEntity> {

    /**
     * Returns the structure of the grid, including its dimensions and valid coordinates.
     *
     * @return the grid structure
     */
    GridStructure structure();

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
     */
    T getEntity(GridCoordinate coordinate);

    /**
     * Checks whether the entity at the specified coordinate is equal to the default entity.
     *
     * @param coordinate the grid coordinate to check
     * @return {@code true} if the entity at the coordinate is the default entity, {@code false} otherwise
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
     * Checks if the given coordinate is valid within the grid structure.
     *
     * @param coordinate the grid coordinate
     * @return true if the coordinate is valid, false otherwise
     */
    default boolean isCoordinateValid(GridCoordinate coordinate) {
        return structure().isCoordinateValid(coordinate);
    }

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
     * Returns a map of coordinates to entities for which the predicate returns true.
     *
     * @param isValid predicate to filter entities
     * @return a map of coordinates to entities
     */
    // TODO Optimize and rename the method as soon as it is used later.
    default Map<GridCoordinate, T> toMap(Predicate<T> isValid) {
        Map<GridCoordinate, T> result = new LinkedHashMap<>();
        for (GridCoordinate coordinate : structure().coordinatesList()) {
            T entity = getEntity(coordinate);
            if (isValid.test(entity)) {
                result.put(coordinate, entity);
            }
        }
        return result;
    }

    /**
     * Returns a map of coordinates to entities that are not equal to the default entity.
     *
     * @return a map of coordinates to non-default entities
     */
    // TODO Optimize and rename the method as soon as it is used later.
    default Map<GridCoordinate, T> toMap() {
        return toMap(entity -> !Objects.equals(entity, defaultEntity()));
    }

    /**
     * Returns a stream of all entities in the grid.
     *
     * @return a stream of grid entities
     */
    // TODO Optimize and rename the method as soon as it is used later.
    default Stream<T> entitiesAsStream() {
        return structure().coordinatesStream().map(this::getEntity);
    }

    /**
     * Checks if all grid cells contain the default entity.
     *
     * @return true if the grid is empty, false otherwise
     */
    // TODO Optimize and rename the method as soon as it is used later.
    default boolean isEmpty() {
        return entitiesAsStream().allMatch(entity -> Objects.equals(entity, defaultEntity()));
    }

    /**
     * Returns a stream of all grid cells (coordinate and entity).
     *
     * @return a stream of GridCell<T>
     */
    // TODO Optimize and rename the method as soon as it is used later.
    // TODO Optimize performance
    default Stream<GridCell<T>> cellsAsStream() {
        return structure().coordinatesStream()
                          .map(coordinate -> new GridCell<>(coordinate, getEntity(coordinate)));
    }

    /**
     * Returns a collection of all grid cells (coordinate and entity).
     *
     * @return a collection of GridCell<T>
     */
    // TODO Optimize and rename the method as soon as it is used later.
    default Collection<GridCell<T>> cellsAsCollection() {
        return cellsAsStream().toList();
    }

    /**
     * Returns a stream of grid cells whose entity is not the default entity.
     *
     * @return a stream of non-default GridCell<T>
     */
    // TODO Optimize and rename the method as soon as it is used later.
    default Stream<GridCell<T>> nonDefaultCells() {
        T def = defaultEntity();
        return cellsAsStream().filter(cell -> !Objects.equals(cell.entity(), def));
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
     * Finds the first grid cell that matches the given predicate.
     * <p>
     * This method filters the stream of all grid cells using the provided predicate
     * and returns the first matching cell wrapped in an {@link Optional}. If no cell
     * matches the predicate, {@code Optional.empty()} is returned.
     *
     * @param predicate the condition to test each grid cell against
     * @return an {@link Optional} containing the first matching {@link GridCell}, or empty if none match
     */
    // TODO Optimize and rename the method as soon as it is used later.
    default Optional<GridCell<T>> findCell(Predicate<? super GridCell<T>> predicate) {
        return cellsAsStream().filter(predicate).findFirst();
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
    // TODO Optimize and rename the method as soon as it is used later.
    // TODO Optimize performance
    default long count(Predicate<? super GridCell<T>> predicate) {
        return cellsAsStream().filter(predicate).count();
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
        return cellsAsStream()
                .filter(cell -> entityPredicate.test(cell.entity()))
                .sorted(cellOrdering)
                .toList();
    }

}
