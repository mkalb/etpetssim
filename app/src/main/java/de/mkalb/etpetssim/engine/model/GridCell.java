package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Immutable default implementation of {@link GridCellView} that binds a
 * {@link GridCoordinate} to exactly one {@link GridEntity}.
 * <p>
 * This record is the canonical engine-level cell value for single-layer grid
 * models.
 *
 * @param <T> the concrete entity type stored in the cell
 * @param coordinate the grid coordinate of this cell
 * @param entity the entity stored at {@code coordinate}
 */
public record GridCell<T extends GridEntity>(GridCoordinate coordinate, T entity)
        implements GridCellView<T> {}
