package de.mkalb.etpetssim.simulations.etpets.model.entity;

/**
 * Contract for terrain entities in the ET Pets simulation.
 */
public sealed interface TerrainEntity extends EtpetsEntity
        permits TerrainConstant, Trail {

    /**
     * Indicates whether an agent can move onto this terrain entity.
     *
     * @return {@code true} when the terrain is walkable
     */
    boolean isWalkable();

    @Override
    default boolean isEmpty() {
        return false;
    }

}
