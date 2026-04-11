package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum NoResource implements ResourceEntity, ConstantGridEntity {

    NO_RESOURCE;

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_NO_RESOURCE;
    }

    @Override
    public boolean isResource() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

}
