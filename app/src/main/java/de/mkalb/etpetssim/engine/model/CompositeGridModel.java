package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

import java.util.*;

/**
 * Represents a grid model composed of multiple sub-models or layers.
 * <p>
 * Provides a unified interface for grid models that aggregate or combine other grid models,
 * such as layered or composite grids. All sub-models must share the same grid structure.
 * </p>
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public non-sealed interface CompositeGridModel<T extends GridEntity> extends GridModel<T> {

    /**
     * Returns a list of entities at the specified coordinate.
     *
     * @param coordinate the grid coordinate
     * @return a list of entities at the coordinate
     */
    List<T> getEntities(GridCoordinate coordinate);

    /**
     * Returns the number of sub-models or layers contained in this composite grid model.
     *
     * @return the count of sub-models
     */
    int subModelCount();

    @Override
    default boolean isComposite() {
        return true;
    }

}
