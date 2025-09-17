package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

/**
 * A record representing a cell in a grid model, associating a coordinate with an entity.
 *
 * @param <T> the type of entity stored in the cell, must implement {@link GridEntity}
 * @param coordinate the coordinate of the cell in the grid
 * @param entity the entity associated with the cell
 *
 * @see WritableGridModel
 */
public record GridCell<T extends GridEntity>(GridCoordinate coordinate, T entity) {

    /**
     * Returns a short, human-readable string representation of this grid cell.
     * <p>
     * Format: {@code (x, y) [Entity]}
     * <br>
     * Example: {@code (10, 20) [WALL]}
     *
     * @return a concise display string for this grid cell
     */
    public String toDisplayString() {
        return String.format("%s %s",
                coordinate.toDisplayString(),
                entity.toDisplayString());
    }

}
