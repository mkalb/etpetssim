package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum NoResource implements ResourceEntity, ConstantGridEntity {

    NO_RESOURCE;

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return DESCRIPTOR_ID_NO_RESOURCE;
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
