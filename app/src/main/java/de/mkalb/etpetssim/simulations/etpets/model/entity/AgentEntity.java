package de.mkalb.etpetssim.simulations.etpets.model.entity;

/**
 * Contract for agent entities in the ET Pets simulation.
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

    /**
     * Returns the identifier of this agent.
     *
     * <p>The default is {@code 0}, used by agents without an identity such as {@link NoAgent}.
     *
     * @return the agent identifier
     */
    default int id() {
        return 0;
    }

}
