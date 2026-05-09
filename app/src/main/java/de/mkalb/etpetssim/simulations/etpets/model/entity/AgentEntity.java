package de.mkalb.etpetssim.simulations.etpets.model.entity;

/**
 * Marker contract for agent entities in the ET Pets simulation.
 */
public sealed interface AgentEntity extends EtpetsEntity
        permits NoAgent, Pet, PetEgg {

    @Override
    default boolean isTerrain() {
        return false;
    }

    @Override
    default boolean isResource() {
        return false;
    }

}
