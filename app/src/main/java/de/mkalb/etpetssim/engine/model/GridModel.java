package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;

/**
 * Base interface for grid models, providing access to grid structure and coordinate validation.
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public sealed interface GridModel<T extends GridEntity>
        permits ReadableGridModel, CompositeGridModel {

    /**
     * Returns the structure of the grid, including its dimensions and valid coordinates.
     *
     * @return the grid structure
     */
    GridStructure structure();

    /**
     * Checks if the given coordinate is valid within the grid structure.
     *
     * @param coordinate the grid coordinate
     * @return true if the coordinate is valid, false otherwise
     */
    default boolean isCoordinateValid(GridCoordinate coordinate) {
        return structure().isCoordinateValid(coordinate);
    }

}
