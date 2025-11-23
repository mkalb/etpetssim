package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.ConstantGridEntity;

public enum SugarResourceNone implements SugarResourceEntity, ConstantGridEntity {

    NONE;

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return DESCRIPTOR_ID_RESOURCE_NONE;
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
