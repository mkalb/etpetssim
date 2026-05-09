package de.mkalb.etpetssim.simulations.wator.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

/**
 * Terrain cell state for the Wa-Tor simulation.
 * <p>
 * Represents water, the base terrain in which fish and sharks exist.
 */
public enum TerrainConstant implements WatorEntity, ConstantGridEntity {

    WATER(WatorEntity.DESCRIPTOR_ID_WATER);

    private final String descriptorId;

    @SuppressWarnings("SameParameterValue")
    TerrainConstant(String descriptorId) {
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
    public boolean isAgent() {
        return false;
    }

    @Override
    public boolean isFish() {
        return false;
    }

    @Override
    public boolean isShark() {
        return false;
    }

    @Override
    public final boolean isWater() {
        return this == WATER;
    }

}
