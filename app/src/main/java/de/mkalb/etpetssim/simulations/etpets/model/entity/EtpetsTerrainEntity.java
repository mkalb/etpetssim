package de.mkalb.etpetssim.simulations.etpets.model.entity;

public sealed interface EtpetsTerrainEntity extends EtpetsEntity
        permits EtpetsTerrainConstant, EtpetsTerrainTrail {

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

}

