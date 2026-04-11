package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum NoAgent implements AntEntity, ConstantGridEntity {

    NO_AGENT;

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public String descriptorId() {
        return AntEntity.DESCRIPTOR_ID_NO_AGENT;
    }

}
