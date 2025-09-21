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
     * Indicates whether this grid model is a composite model that aggregates multiple sub-models or layers.
     *
     * @return {@code true} if the grid model is composite, {@code false} otherwise
     */
    boolean isComposite();

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
