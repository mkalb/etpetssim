package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

/**
 * Represents the absence of an agent in the Sugarscape simulation.
 * <p>
 * This singleton enum is used as a placeholder when a cell does not contain any agent entity.
 */
public enum NoAgent implements AgentEntity, ConstantGridEntity {

    NO_AGENT;

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return DESCRIPTOR_ID_NO_AGENT;
    }

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

}
