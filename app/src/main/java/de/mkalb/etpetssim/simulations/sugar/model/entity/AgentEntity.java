package de.mkalb.etpetssim.simulations.sugar.model.entity;

/**
 * Contract for agent entities in the Sugarscape simulation.
 */
public sealed interface AgentEntity extends SugarEntity
        permits Agent, NoAgent {
}
