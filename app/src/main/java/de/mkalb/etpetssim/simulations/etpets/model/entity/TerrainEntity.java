package de.mkalb.etpetssim.simulations.etpets.model.entity;

/**
 * Contract for terrain entities in the ET Pets simulation.
 */
public sealed interface TerrainEntity extends EtpetsEntity
        permits TerrainConstant, Trail {

    @Override
    default boolean isTerrain() {
        return true;
    }

    @Override
    default boolean isResource() {
        return false;
    }

    @Override
    default boolean isAgent() {
        return false;
    }

    @Override
    default boolean isEmpty() {
        return false;
    }

    /**
     * Indicates whether an agent can move onto this terrain entity.
     *
     * @return {@code true} when the terrain is walkable
     */
    boolean isWalkable();

}
