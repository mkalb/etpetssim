package de.mkalb.etpetssim.simulations.etpets.model.entity;

/**
 * Marker contract for resource entities in the ET Pets simulation.
 */
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
