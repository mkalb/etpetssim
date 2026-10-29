package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.Locale;

/**
 * Read-only view of a grid cell that exposes its coordinate and associated entity.
 *
 * <p>Provides shared convenience methods for cell-like value types
 * (for example simple engine cells and simulation-specific composite cells).
 *
 * @param <ENT> the concrete entity type exposed by this cell view
 */
public interface GridCellView<ENT extends GridEntity> {

    GridCoordinate coordinate();

    ENT entity();

    /**
     * Returns the unique descriptor ID of the associated entity.
     *
     * <p>Convenience shortcut for {@link GridEntity#descriptorId()}.
     *
     * @return the entity descriptor ID
     */
    default String descriptorId() {
        return entity().descriptorId();
    }

    /**
     * Returns a short, human-readable display string for this cell.
     *
     * <p>Format: {@code <coordinate-display> <entity-display>}, for example {@code (10, 20) [WALL]}.
     *
     * @return a concise display string for this cell
     */
    default String toDisplayString() {
        return String.format(Locale.ROOT, "%s %s",
                coordinate().toDisplayString(),
                entity().toDisplayString());
    }

}
