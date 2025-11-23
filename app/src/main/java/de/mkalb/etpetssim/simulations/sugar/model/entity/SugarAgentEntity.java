package de.mkalb.etpetssim.simulations.sugar.model.entity;

public sealed interface SugarAgentEntity extends SugarEntity
        permits SugarAgent, SugarAgentNone {

    @Override
    default boolean isTerrain() {
        return false;
    }

    @Override
    default boolean isResource() {
        return false;
    }

}
