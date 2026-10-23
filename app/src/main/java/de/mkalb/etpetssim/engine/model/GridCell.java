package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Default implementation of {@link GridCellView} that binds a {@link GridCoordinate} to exactly one
 * {@link GridEntity}.
 *
 * <p>This record is the canonical engine-level cell value for single-layer grid models.
 *
 * <p>The record holds the entity reference without copying the entity; later changes to a mutable entity are
 * visible through the record.
 *
 * @param <ENT>      the concrete entity type stored in the cell
 * @param coordinate the grid coordinate of this cell
 * @param entity     the entity stored at {@code coordinate}
 */
public record GridCell<ENT extends GridEntity>(GridCoordinate coordinate, ENT entity)
        implements GridCellView<ENT> {
}
