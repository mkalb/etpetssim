package de.mkalb.etpetssim.simulations.sugar.model.entity;

/**
 * Marker contract for resource entities in the Sugar simulation.
 */
public sealed interface ResourceEntity extends SugarEntity
        permits Sugar, NoResource {

    @Override
    default boolean isAgent() {
        return false;
    }

}
