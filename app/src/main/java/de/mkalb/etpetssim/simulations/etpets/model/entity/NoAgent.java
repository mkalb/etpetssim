package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum NoAgent implements AgentEntity, ConstantGridEntity {

    NONE;

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_AGENT_NONE;
    }

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public boolean isNone() {
        return true;
    }

}
