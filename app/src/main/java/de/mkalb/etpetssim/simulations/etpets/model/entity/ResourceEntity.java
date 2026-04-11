package de.mkalb.etpetssim.simulations.etpets.model.entity;

public sealed interface ResourceEntity extends EtpetsEntity
        permits NoResource, ResourceBase {

    @Override
    default boolean isTerrain() {
        return false;
    }

    @Override
    default boolean isAgent() {
        return false;
    }

}
