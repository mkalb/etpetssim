package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

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
    public boolean isNone() {
        return true;
    }

}
