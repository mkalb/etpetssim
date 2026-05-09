package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.CompositeGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;

import java.util.*;

/**
 * Composite grid model for ET Pets with terrain, resource, and agent layers.
 *
 * @param structure the shared grid structure of all sub-models
 * @param terrainModel the terrain layer
 * @param resourceModel the resource layer
 * @param agentModel the agent layer
 */
public record EtpetsGridModel(
        GridStructure structure,
        WritableGridModel<TerrainEntity> terrainModel,
        WritableGridModel<ResourceEntity> resourceModel,
        WritableGridModel<AgentEntity> agentModel)
        implements CompositeGridModel<EtpetsEntity> {

    public EtpetsGridModel {
        if (!structure.equals(terrainModel.structure())
                || !structure.equals(resourceModel.structure())
                || !structure.equals(agentModel.structure())) {
            throw new IllegalArgumentException("All structures must be the same");
        }
    }

    @Override
    public List<EtpetsEntity> getEntities(GridCoordinate coordinate) {
        return List.of(
                terrainModel.getEntity(coordinate),
                resourceModel.getEntity(coordinate),
                agentModel.getEntity(coordinate)
        );
    }

    @Override
    public int subModelCount() {
        return 3;
    }

}

