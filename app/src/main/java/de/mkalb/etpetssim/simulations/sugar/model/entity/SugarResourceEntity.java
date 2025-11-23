package de.mkalb.etpetssim.simulations.sugar.model.entity;

public sealed interface SugarResourceEntity extends SugarEntity
        permits SugarResourceSugar, SugarResourceNone {

    @Override
    default boolean isTerrain() {
        return false;
    }

    @Override
    default boolean isAgent() {
        return false;
    }

}
