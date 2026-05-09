package de.mkalb.etpetssim.simulations.sugar.model.entity;

/**
 * Marker contract for agent entities in the Sugar simulation.
 */
public sealed interface AgentEntity extends SugarEntity
        permits Agent, NoAgent {

    @Override
    default boolean isResource() {
        return false;
    }

}
