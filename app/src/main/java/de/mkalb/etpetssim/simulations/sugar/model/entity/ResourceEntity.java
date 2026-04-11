package de.mkalb.etpetssim.simulations.sugar.model.entity;

public sealed interface ResourceEntity extends SugarEntity
        permits Sugar, NoResource {

    @Override
    default boolean isAgent() {
        return false;
    }

}
