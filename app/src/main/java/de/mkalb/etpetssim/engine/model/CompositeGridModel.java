package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;

/**
 * Represents a grid model composed of multiple layers.
 * <p>
 * Provides a unified interface for grid models that aggregate or combine other grid models,
 * such as layered or composite grids. All layers must share the same grid structure.
 * </p>
 *
 * @param <T> the type of entities stored in the grid, must implement {@link de.mkalb.etpetssim.engine.model.entity.GridEntity}
 */
public non-sealed interface CompositeGridModel<T extends GridEntity> extends GridModel<T> {

    /**
     * Returns the entities at the specified coordinate across all layers.
     * <p>
     * The list ordering is implementation-defined and should be documented by each implementation.
     * </p>
     *
     * @param coordinate the grid coordinate
     * @return a list of entities at the coordinate, one entry per layer
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    List<T> getEntities(GridCoordinate coordinate);

    /**
     * Returns the number of layers contained in this composite grid model.
     *
     * @return the count of layers
     */
    int layerCount();

    @Override
    default boolean isComposite() {
        return true;
    }

}
