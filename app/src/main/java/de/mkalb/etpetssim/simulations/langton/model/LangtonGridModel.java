package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.CompositeGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.langton.model.entity.AntEntity;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import de.mkalb.etpetssim.simulations.langton.model.entity.TerrainConstant;

import java.util.*;

/**
 * Composite grid model for Langton's Ant with separate ground and ant layers.
 *
 * @param structure the shared grid structure of all sub-models
 * @param groundModel the terrain/state layer
 * @param antModel the ant layer
 */
public record LangtonGridModel(
        GridStructure structure,
        WritableGridModel<TerrainConstant> groundModel,
        WritableGridModel<AntEntity> antModel)
        implements CompositeGridModel<LangtonEntity> {

    public LangtonGridModel {
        if (!structure.equals(groundModel.structure()) || !structure.equals(antModel.structure())) {
            throw new IllegalArgumentException("All structures must be the same");
        }
    }

    @Override
    public List<LangtonEntity> getEntities(GridCoordinate coordinate) {
        return List.of(groundModel.getEntity(coordinate), antModel.getEntity(coordinate));
    }

    @Override
    public int layerCount() {
        return 2;
    }

}
