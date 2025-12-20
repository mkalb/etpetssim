package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum SugarAgentNone implements SugarAgentEntity, ConstantGridEntity {

    NONE;

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return DESCRIPTOR_ID_AGENT_NONE;
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
