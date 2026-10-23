package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;

import java.util.*;

/**
 * Snapshot of all Langton layers for a single grid coordinate.
 *
 * <p>The record stores both layer entities (terrain, ant) and exposes an effective top-most entity via
 * {@link #entity()}.
 *
 * <p>The record holds the entity references read at creation without copying the entities; later changes to a
 * mutable {@link Ant} are visible through the record.
 *
 * @param coordinate    coordinate of this snapshot
 * @param terrainEntity terrain layer entity
 * @param antEntity     ant layer entity
 */
public record LangtonCell(GridCoordinate coordinate,
                          TerrainConstant terrainEntity,
                          AntEntity antEntity)
        implements GridCellView<LangtonEntity> {

    /**
     * Creates a layered Langton cell snapshot from the given grid model.
     *
     * @param model      the Langton grid model providing all layers
     * @param coordinate the coordinate to read
     * @return the composed cell snapshot for the coordinate
     */
    public static LangtonCell from(LangtonGridModel model, GridCoordinate coordinate) {
        return new LangtonCell(coordinate,
                model.terrainModel().getEntity(coordinate),
                model.antModel().getEntity(coordinate));
    }

    /**
     * Returns the effective entity visible for this cell.
     *
     * <p>Priority order is: ant, then terrain.
     *
     * @return the ant if present, otherwise the terrain entity
     */
    @Override
    public LangtonEntity entity() {
        if (antEntity.isAgent()) {
            return antEntity;
        }
        return terrainEntity;
    }

    /**
     * Returns a display string containing the coordinate and both layer entities (terrain, ant).
     *
     * <p>Format: {@code <coordinate-display> <terrain-display> <ant-display>}, for example
     * {@code (1, 2) [COLOR_1] [NO_AGENT]}.
     *
     * @return a layer-complete display string for this Langton cell
     */
    @Override
    public String toDisplayString() {
        return String.format(Locale.ROOT, "%s %s %s",
                coordinate.toDisplayString(),
                terrainEntity.toDisplayString(),
                antEntity.toDisplayString());
    }

}
