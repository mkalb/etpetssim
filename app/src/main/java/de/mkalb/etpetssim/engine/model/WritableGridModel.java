package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.function.*;

/**
 * Represents a writable grid model that stores entities at grid coordinates.
 * <p>
 * Provides mutating (write and update) methods for grid entities.
 * All read-only methods are defined in the {@link ReadableGridModel} interface.
 * </p>
 *
 * @param <T> the type of entities stored in the grid, must implement {@link de.mkalb.etpetssim.engine.model.entity.GridEntity}
 */
public sealed interface WritableGridModel<T extends GridEntity> extends ReadableGridModel<T>
        permits ArrayGridModel, SparseGridModel {

    /**
     * Creates a copy of this grid model, including the current grid state.
     * <p>
     * The returned model has independent internal storage, but entity instances may be shared,
     * depending on the concrete model and entity type.
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
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    void setEntity(GridCoordinate coordinate, T entity);

    /**
     * Sets the entity in the grid using a {@link GridCell}.
     * <p>
     * This method extracts the coordinate and entity from the provided {@link GridCell}
     * and sets the entity at the corresponding coordinate in the grid.
     *
     * @param cell the {@link GridCell} containing the coordinate and entity to set
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    default void setEntity(GridCell<T> cell) {
        setEntity(cell.coordinate(), cell.entity());
    }

    /**
     * Sets the entity at the specified coordinate to the default entity.
     *
     * @param coordinate the grid coordinate
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    void setEntityToDefault(GridCoordinate coordinate);

    /**
     * Sets all grid cells to the specified entity.
     *
     * @param entity the entity to set
     */
    void fill(T entity);

    /**
     * Sets all grid cells using a supplier that provides a new entity for each cell.
     * The supplier is invoked exactly once per cell; invocation order is implementation-defined.
     *
     * @param supplier the supplier to generate entities for each coordinate
     */
    void fill(Supplier<T> supplier);

    /**
     * Sets all grid cells using a mapping function from coordinate to entity.
     * The mapper is invoked exactly once per cell; invocation order is implementation-defined.
     *
     * @param mapper the function to compute entities for each coordinate
     */
    void fill(Function<GridCoordinate, T> mapper);

    /**
     * Sets all grid cells to the default entity.
     */
    void clear();

    /**
     * Writes swapped entity values from the two given {@link GridCell} objects.
     * <p>
     * Uses {@code cellA.entity()} and {@code cellB.entity()} as input values and writes them to the
     * opposite coordinates ({@code cellA.coordinate()} receives {@code cellB.entity()}, and vice versa).
     * Both coordinates are validated before any write is performed.
     * The current model state at those coordinates is not read.
     *
     * @param cellA the first input cell providing one coordinate and one entity value
     * @param cellB the second input cell providing one coordinate and one entity value
     * @throws IndexOutOfBoundsException if either coordinate is not valid
     */
    void swapInputCellEntities(GridCell<T> cellA, GridCell<T> cellB);

}
