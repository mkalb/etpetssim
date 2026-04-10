package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum NoResource implements ResourceEntity, ConstantGridEntity {

    NONE;

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_RESOURCE_NONE;
    }

    @Override
    public boolean isResource() {
        return false;
    }

    @Override
    public boolean isNone() {
        return true;
    }

}

