package de.mkalb.etpetssim.simulations.etpets.model.entity;

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
    default boolean isNone() {
        return false;
    }

    default boolean isBlockingLineOfSight() {
        return this == TerrainConstant.ROCK;
    }

    boolean isWalkable();

}
