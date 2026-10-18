package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

/**
 * Represents the absence of a pet in the ET Pets simulation.
 *
 * <p>This singleton enum is used as a placeholder when a cell does not contain any agent entity.
 */
public enum NoAgent implements AgentEntity, ConstantGridEntity {

    NO_AGENT;

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_NO_AGENT;
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
