package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Read-only view of a grid cell that exposes its coordinate and associated entity.
 * <p>
 * This interface provides shared convenience methods for cell-like value types
 * (for example simple engine cells and simulation-specific composite cells).
 *
 * @param <T> the concrete entity type exposed by this cell view
 */
public interface GridCellView<T extends GridEntity> {

    /**
     * Returns the coordinate of this cell.
     *
     * @return the cell coordinate
     */
    GridCoordinate coordinate();

    /**
     * Returns the entity represented by this cell view.
     *
     * @return the associated entity
     */
    T entity();

    /**
     * Returns the unique descriptor ID of the associated entity.
     * <p>
     * Convenience shortcut for {@link GridEntity#descriptorId()}.
     *
     * @return the entity descriptor ID
     */
    default String descriptorId() {
        return entity().descriptorId();
    }

    /**
     * Returns a short, human-readable display string for this cell.
     * <p>
     * Format: {@code <coordinate-display> <entity-display>}.
     * <br>
     * Example: {@code (10, 20) [WALL]}
     *
     * @return a concise display string for this cell
     */
    default String toDisplayString() {
        return String.format("%s %s",
                coordinate().toDisplayString(),
                entity().toDisplayString());
    }

}
