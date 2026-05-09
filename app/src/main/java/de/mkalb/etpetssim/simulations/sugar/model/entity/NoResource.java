package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

/**
 * Represents the absence of a resource in the Sugarscape simulation.
 * <p>
 * This singleton enum is used as a placeholder when a cell does not contain any resource entity.
 */
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
    public boolean isEmpty() {
        return true;
    }

}
