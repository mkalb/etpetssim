package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;

/**
 * Snapshot of all ET-Pets layers for a single grid coordinate.
 * <p>
 * The record stores all three layer entities (terrain, resource, agent) and
 * exposes an effective top-most entity via {@link #entity()}.
 *
 * @param coordinate     coordinate of this snapshot
 * @param terrainEntity  terrain layer entity
 * @param resourceEntity resource layer entity
 * @param agentEntity    agent layer entity
 */
public record EtpetsCell(GridCoordinate coordinate,
                         TerrainEntity terrainEntity,
                         ResourceEntity resourceEntity,
                         AgentEntity agentEntity)
        implements GridCellView<EtpetsEntity> {

    /**
     * Creates a layered ET-Pets cell snapshot from the given grid model.
     *
     * @param model      the ET-Pets grid model providing all layers
     * @param coordinate the coordinate to read
     * @return the composed cell snapshot for the coordinate
     */
    public static EtpetsCell of(EtpetsGridModel model, GridCoordinate coordinate) {
        return new EtpetsCell(coordinate,
                model.terrainModel().getEntity(coordinate),
                model.resourceModel().getEntity(coordinate),
                model.agentModel().getEntity(coordinate));
    }

    /**
     * Returns the effective entity visible for this cell.
     * <p>
     * Priority order is: agent, then resource, then terrain.
     *
     * @return the top-most non-empty ET-Pets entity
     */
    @Override
    public EtpetsEntity entity() {
        if (agentEntity.isNotEmpty()) {
            return agentEntity;
        } else if (resourceEntity.isNotEmpty()) {
            return resourceEntity;
        } else {
            return terrainEntity;
        }
    }

    /**
     * Returns a display string containing the coordinate and all three layer
     * entities (terrain, resource, agent).
     * <p>
     * Format:
     * {@code <coordinate> <terrain-display> <resource-display> <agent-display>}
     *
     * @return a layer-complete display string for this ET-Pets cell
     */
    @Override
    public String toDisplayString() {
        return String.format("%s %s %s %s",
                coordinate.toDisplayString(),
                terrainEntity.toDisplayString(),
                resourceEntity.toDisplayString(),
                agentEntity.toDisplayString());
    }

    /**
     * Returns true if the cell is traversable and currently unoccupied:
     * walkable terrain, no resource, no agent.
     *
     * @return true if the cell can be walked on by an agent
     */
    public boolean isWalkable() {
        return terrainEntity.isWalkable() && resourceEntity.isEmpty() && agentEntity.isEmpty();
    }

}
