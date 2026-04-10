package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsAgentEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsTerrainEntity;

/**
 * Snapshot of all ET-Pets layers for a single grid coordinate.
 *
 * @param coordinate coordinate of this snapshot.
 * @param terrainEntity terrain layer entity.
 * @param resourceEntity resource layer entity.
 * @param agentEntity agent layer entity.
 */
public record EtpetsCell(GridCoordinate coordinate,
                         EtpetsTerrainEntity terrainEntity,
                         EtpetsResourceEntity resourceEntity,
                         EtpetsAgentEntity agentEntity) {

    public static EtpetsCell of(GridCoordinate coordinate, EtpetsGridModel model) {
        return new EtpetsCell(coordinate,
                model.terrainModel().getEntity(coordinate),
                model.resourceModel().getEntity(coordinate),
                model.agentModel().getEntity(coordinate));
    }

    /**
     * Returns true if the cell is traversable and currently unoccupied:
     * walkable terrain, no resource, no agent.
     */
    public boolean isWalkable() {
        return terrainEntity.isWalkable() && resourceEntity.isNone() && agentEntity.isNone();
    }

}
