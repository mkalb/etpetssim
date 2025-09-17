package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

import java.util.function.*;

/**
 * Represents a writable grid model that stores entities at grid coordinates.
 * <p>
 * Provides mutating (write and update) methods for grid entities.
 * All read-only methods are defined in the {@link ReadableGridModel} interface.
 * </p>
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public non-sealed interface WritableGridModel<T extends GridEntity> extends ReadableGridModel<T> {

    /**
     * Creates a deep copy of this grid model, including all entities.
     *
     * @return a copy of this grid model
     */
    WritableGridModel<T> copy();

    /**
     * Creates a copy of this grid model with all entities set to the default entity.
     *
     * @return a blank copy of this grid model
     */
    WritableGridModel<T> copyWithDefaultEntity();

    /**
     * Sets the entity at the specified coordinate.
     *
     * @param coordinate the grid coordinate
     * @param entity the entity to set
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    void setEntity(GridCoordinate coordinate, T entity);

    /**
     * Sets the entity in the grid using a {@link GridCell}.
     * <p>
     * This method extracts the coordinate and entity from the provided {@link GridCell}
     * and sets the entity at the corresponding coordinate in the grid.
     *
     * @param cell the {@link GridCell} containing the coordinate and entity to set
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    default void setEntity(GridCell<T> cell) {
        setEntity(cell.coordinate(), cell.entity());
    }

    /**
     * Sets the entity at the specified coordinate to the default entity.
     *
     * @param coordinate the grid coordinate
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    default void setEntityToDefault(GridCoordinate coordinate) {
        setEntity(coordinate, defaultEntity());
    }

    /**
     * Sets all grid cells to the specified entity.
     * <p>
     * Should be overwritten by subclasses to optimize performance.
     *
     * @param entity the entity to set
     */
    default void fill(T entity) {
        structure().coordinatesStream().forEachOrdered(coordinate -> setEntity(coordinate, entity));
    }

    /**
     * Sets all grid cells using a supplier that provides a new entity for each cell.
     *
     * @param supplier the supplier to generate entities for each coordinate
     */
    default void fill(Supplier<T> supplier) {
        structure().coordinatesStream().forEachOrdered(coordinate -> setEntity(coordinate, supplier.get()));
    }

    /**
     * Sets all grid cells using a mapping function from coordinate to entity.
     *
     * @param mapper the function to compute entities for each coordinate
     */
    default void fill(Function<GridCoordinate, T> mapper) {
        structure().coordinatesStream().forEachOrdered(coordinate -> setEntity(coordinate, mapper.apply(coordinate)));
    }

    /**
     * Sets all grid cells to the default entity.
     * <p>
     * Should be overwritten by subclasses to optimize performance.
     */
    default void clear() {
        fill(defaultEntity());
    }

    /**
     * Swaps the entities at the coordinates of the two given {@link GridCell} objects.
     * <p>
     * Should be overwritten by subclasses to optimize performance.
     *
     * @param cellA the first grid cell whose entity and coordinate are involved in the swap
     * @param cellB the second grid cell whose entity and coordinate are involved in the swap
     * @throws IndexOutOfBoundsException if either coordinate is not valid in this grid
     */
    default void swapEntities(GridCell<T> cellA, GridCell<T> cellB) {
        T entityA = cellA.entity();
        T entityB = cellB.entity();
        setEntity(cellA.coordinate(), entityB);
        setEntity(cellB.coordinate(), entityA);
    }

}
