package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

import java.util.*;

/**
 * A record representing a cell in a grid model, associating a coordinate with an entity.
 *
 * @param <T> the type of entity stored in the cell, must implement {@link GridEntity}
 * @param coordinate the coordinate of the cell in the grid
 * @param entity the entity associated with the cell
 *
 * @see GridModel
 */
public record GridCell<T extends GridEntity>(GridCoordinate coordinate, T entity) {

    /**
     * Constructs a new GridCell instance.
     * Ensures that neither the coordinate nor the entity is null.
     *
     * @param coordinate the coordinate of the cell in the grid
     * @param entity the entity associated with the cell
     */
    public GridCell {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(entity);
    }

}
