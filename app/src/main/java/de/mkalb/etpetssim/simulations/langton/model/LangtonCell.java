package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;

/**
 * Snapshot of all Langton layers for a single grid coordinate.
 * <p>
 * The record stores both layer entities (ground, ant) and
 * exposes an effective top-most entity via {@link #entity()}.
 *
 * @param coordinate      coordinate of this snapshot
 * @param terrainConstant ground layer entity
 * @param antEntity       ant layer entity
 */
public record LangtonCell(GridCoordinate coordinate,
                          TerrainConstant terrainConstant,
                          AntEntity antEntity)
        implements GridCellView<LangtonEntity> {

    /**
     * Creates a layered Langton cell snapshot from the given grid model.
     *
     * @param model      the Langton grid model providing all layers
     * @param coordinate the coordinate to read
     * @return the composed cell snapshot for the coordinate
     */
    public static LangtonCell of(LangtonGridModel model, GridCoordinate coordinate) {
        return new LangtonCell(coordinate,
                model.groundModel().getEntity(coordinate),
                model.antModel().getEntity(coordinate));
    }

    /**
     * Returns the effective entity visible for this cell.
     * <p>
     * Priority order is: ant, then ground.
     *
     * @return the top-most non-empty Langton entity
     */
    @Override
    public LangtonEntity entity() {
        if (antEntity instanceof Ant) {
            return antEntity;
        }
        return terrainConstant;
    }

    /**
     * Returns a display string containing the coordinate and both layer
     * entities (ground, ant).
     * <p>
     * Format:
     * {@code <coordinate> <ground-display> <ant-display>}
     *
     * @return a layer-complete display string for this Langton cell
     */
    @Override
    public String toDisplayString() {
        return String.format("%s %s %s",
                coordinate.toDisplayString(),
                terrainConstant.toDisplayString(),
                antEntity.toDisplayString());
    }

}