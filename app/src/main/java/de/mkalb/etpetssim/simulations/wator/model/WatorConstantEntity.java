package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.ConstantGridEntity;

public enum WatorConstantEntity implements WatorEntity, ConstantGridEntity {

    WATER(WatorEntity.DESCRIPTOR_ID_WATER);

    private final String descriptorId;

    @SuppressWarnings("SameParameterValue")
    WatorConstantEntity(String descriptorId) {
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

}
