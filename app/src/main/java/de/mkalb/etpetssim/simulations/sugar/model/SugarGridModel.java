package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.CompositeGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.sugar.model.entity.AgentEntity;
import de.mkalb.etpetssim.simulations.sugar.model.entity.ResourceEntity;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;

import java.util.List;

/**
 * Composite grid model for Sugarscape with resource and agent layers.
 *
 * <p>The record groups the writable layers of a running simulation without copying them; it has no state of its
 * own.
 *
 * @param structure     the shared grid structure of all layers
 * @param resourceModel the resource layer
 * @param agentModel    the agent layer
 */
public record SugarGridModel(
        GridStructure structure,
        WritableGridModel<ResourceEntity> resourceModel,
        WritableGridModel<AgentEntity> agentModel)
        implements CompositeGridModel<SugarEntity> {

    /**
     * Creates a composite grid model from layers with the given structure.
     *
     * @throws IllegalArgumentException if the structure of a layer differs from {@code structure}
     */
    public SugarGridModel {
        if (!structure.equals(resourceModel.structure()) || !structure.equals(agentModel.structure())) {
            throw new IllegalArgumentException("All structures must be the same");
        }
    }

    /**
     * Returns the entities at the specified coordinate in the order resource, agent.
     *
     * @param coordinate the grid coordinate
     * @return the two layer entities at the coordinate
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    @Override
    public List<SugarEntity> getEntities(GridCoordinate coordinate) {
        return List.of(resourceModel.getEntity(coordinate), agentModel.getEntity(coordinate));
    }

    @Override
    public int layerCount() {
        return 2;
    }

}
