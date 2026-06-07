package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;

import java.util.*;

/**
 * Composite grid model for Sugarscape with resource and agent layers.
 *
 * @param structure     the shared grid structure of all sub-models
 * @param resourceModel the terrain/resource layer
 * @param agentModel    the agent layer
 */
public record SugarGridModel(
        GridStructure structure,
        WritableGridModel<ResourceEntity> resourceModel,
        WritableGridModel<AgentEntity> agentModel)
        implements CompositeGridModel<SugarEntity> {

    public SugarGridModel {
        if (!structure.equals(resourceModel.structure()) || !structure.equals(agentModel.structure())) {
            throw new IllegalArgumentException("All structures must be the same");
        }
    }

    @Override
    public List<SugarEntity> getEntities(GridCoordinate coordinate) {
        return List.of(resourceModel.getEntity(coordinate), agentModel.getEntity(coordinate));
    }

    @Override
    public int layerCount() {
        return 2;
    }

}
