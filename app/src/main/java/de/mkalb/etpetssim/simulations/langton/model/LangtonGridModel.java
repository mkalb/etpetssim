package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;

import java.util.*;

/**
 * Composite grid model for Langton's Ant with terrain and ant layers.
 *
 * <p>The record groups the writable layers of a running simulation without copying them; it has no state of its
 * own.
 *
 * @param structure    the shared grid structure of all layers
 * @param terrainModel the terrain layer
 * @param antModel     the ant layer
 */
public record LangtonGridModel(
        GridStructure structure,
        WritableGridModel<TerrainConstant> terrainModel,
        WritableGridModel<AntEntity> antModel)
        implements CompositeGridModel<LangtonEntity> {

    /**
     * Creates a composite grid model from layers with the given structure.
     *
     * @throws IllegalArgumentException if the structure of a layer differs from {@code structure}
     */
    public LangtonGridModel {
        if (!structure.equals(terrainModel.structure()) || !structure.equals(antModel.structure())) {
            throw new IllegalArgumentException("All structures must be the same");
        }
    }

    /**
     * Returns the entities at the specified coordinate in the order terrain, ant.
     *
     * @param coordinate the grid coordinate
     * @return the two layer entities at the coordinate
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    @Override
    public List<LangtonEntity> getEntities(GridCoordinate coordinate) {
        return List.of(terrainModel.getEntity(coordinate), antModel.getEntity(coordinate));
    }

    @Override
    public int layerCount() {
        return 2;
    }

}
