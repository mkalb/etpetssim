package de.mkalb.etpetssim.simulations.rebounding.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum ReboundingConstantEntity implements ReboundingEntity, ConstantGridEntity {

    GROUND(ReboundingEntity.DESCRIPTOR_ID_GROUND),
    WALL(ReboundingEntity.DESCRIPTOR_ID_WALL);

    private final String descriptorId;

    ReboundingConstantEntity(String descriptorId) {
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
    public boolean isWall() {
        return this == WALL;
    }

    @Override
    public boolean isMovingEntity() {
        return false;
    }

}
