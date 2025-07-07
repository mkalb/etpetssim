package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

import java.util.function.*;

/**
 * Represents a generic grid model that stores entities at grid coordinates.
 * Provides methods for accessing, modifying, and querying grid entities.
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public interface GridModel<T extends GridEntity> extends ReadableGridModel<T> {

    /**
     * Creates a deep copy of this grid model, including all entities.
     *
     * @return a copy of this grid model
     */
    GridModel<T> copy();

    /**
     * Creates a copy of this grid model with all entities set to the default entity.
     *
     * @return a blank copy of this grid model
     */
    GridModel<T> copyWithDefaultEntity();

    /**
     * Sets the entity at the specified coordinate.
     *
     * @param coordinate the grid coordinate
     * @param entity the entity to set
     */
    void setEntity(GridCoordinate coordinate, T entity);

    /**
     * Sets the entity in the grid using a {@link GridCell}.
     * <p>
     * This method extracts the coordinate and entity from the provided {@link GridCell}
     * and sets the entity at the corresponding coordinate in the grid.
     *
     * @param cell the {@link GridCell} containing the coordinate and entity to set
     */
    default void setEntity(GridCell<T> cell) {
        setEntity(cell.coordinate(), cell.entity());
    }

    /**
     * Sets all grid cells to the default entity.
     * Should be overwritten by subclasses to optimize performance.
     */
    default void clear() {
        fill(defaultEntity());
    }

    /**
     * Sets the entity at the specified coordinate to the default entity.
     *
     * @param coordinate the grid coordinate
     */
    default void setEntityToDefault(GridCoordinate coordinate) {
        setEntity(coordinate, defaultEntity());
    }

    /**
     * Sets all grid cells to the specified entity.
     * Should be overwritten by subclasses to optimize performance.
     *
     * @param entity the entity to set
     */
    default void fill(T entity) {
        structure().coordinatesStream().forEachOrdered(coordinate -> setEntity(coordinate, entity));
    }

    /**
     * Sets all grid cells using a mapping function from coordinate to entity.
     *
     * @param mapper the function to compute entities for each coordinate
     */
    // TODO Optimize and rename the method as soon as it is used later.
    default void fill(Function<GridCoordinate, T> mapper) {
        structure().coordinatesStream().forEachOrdered(coordinate -> setEntity(coordinate, mapper.apply(coordinate)));
    }

}
