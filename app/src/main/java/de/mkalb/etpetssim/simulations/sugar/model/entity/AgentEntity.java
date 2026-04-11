package de.mkalb.etpetssim.simulations.sugar.model.entity;

public sealed interface AgentEntity extends SugarEntity
        permits Agent, NoAgent {

    @Override
    default boolean isResource() {
        return false;
    }

}
