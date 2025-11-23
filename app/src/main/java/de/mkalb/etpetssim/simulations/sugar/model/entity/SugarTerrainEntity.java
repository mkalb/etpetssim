package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.ConstantGridEntity;

public enum SugarTerrainEntity implements SugarEntity, ConstantGridEntity {

    TERRAIN(SugarEntity.DESCRIPTOR_ID_TERRAIN);

    private final String descriptorId;

    @SuppressWarnings("SameParameterValue")
    SugarTerrainEntity(String descriptorId) {
        this.descriptorId = descriptorId;
    }

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return descriptorId;
    }

    @Override
    public boolean isTerrain() {
        return true;
    }

    @Override
    public boolean isResource() {
        return false;
    }

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public boolean isNone() {
        return false;
    }

}
