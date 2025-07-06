package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * A record representing a cell in a grid model.
 *
 * @param <T> The type of the value stored in the cell.
 * @param coordinate The coordinate of the cell in the grid.
 * @param value The value associated with the cell.
 *
 * @see de.mkalb.etpetssim.engine.GridModel
 */
public record GridCell<T>(GridCoordinate coordinate, T value) {

    /**
     * Constructs a new GridCell instance.
     * Ensures that neither the coordinate nor the value is null.
     *
     * @param coordinate The coordinate of the cell in the grid.
     * @param value The value associated with the cell.
     * @throws NullPointerException if either the coordinate or value is null.
     */
    public GridCell {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(value);
    }

}
