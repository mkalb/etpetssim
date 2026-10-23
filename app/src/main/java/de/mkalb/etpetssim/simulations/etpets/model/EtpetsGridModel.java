package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;

import java.util.*;

/**
 * Composite grid model for ET Pets with terrain, resource, and agent layers.
 *
 * <p>The record groups the writable layers of a running simulation without copying them; it has no state of its
 * own.
 *
 * @param structure     the shared grid structure of all layers
 * @param terrainModel  the terrain layer
 * @param resourceModel the resource layer
 * @param agentModel    the agent layer
 */
public record EtpetsGridModel(
        GridStructure structure,
        WritableGridModel<TerrainEntity> terrainModel,
        WritableGridModel<ResourceEntity> resourceModel,
        WritableGridModel<AgentEntity> agentModel)
        implements CompositeGridModel<EtpetsEntity> {

    /**
     * Creates a composite grid model from layers with the given structure.
     *
     * @throws IllegalArgumentException if the structure of a layer differs from {@code structure}
     */
    public EtpetsGridModel {
        if (!structure.equals(terrainModel.structure())
                || !structure.equals(resourceModel.structure())
                || !structure.equals(agentModel.structure())) {
            throw new IllegalArgumentException("All structures must be the same");
        }
    }

    /**
     * Returns the entities at the specified coordinate in the order terrain, resource, agent.
     *
     * @param coordinate the grid coordinate
     * @return the three layer entities at the coordinate
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    @Override
    public List<EtpetsEntity> getEntities(GridCoordinate coordinate) {
        return List.of(
                terrainModel.getEntity(coordinate),
                resourceModel.getEntity(coordinate),
                agentModel.getEntity(coordinate)
        );
    }

    @Override
    public int layerCount() {
        return 3;
    }

}
