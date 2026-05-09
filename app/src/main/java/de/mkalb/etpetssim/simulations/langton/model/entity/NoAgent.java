package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

/**
 * Represents the absence of an ant in Langton's Ant simulation.
 * <p>
 * This singleton enum is used as a placeholder when a cell does not contain any ant entity.
 */
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
