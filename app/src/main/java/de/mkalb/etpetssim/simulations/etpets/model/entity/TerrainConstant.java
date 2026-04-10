package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum TerrainConstant implements TerrainEntity, ConstantGridEntity {

    GROUND(EtpetsEntity.DESCRIPTOR_ID_TERRAIN_GROUND),
    ROCK(EtpetsEntity.DESCRIPTOR_ID_TERRAIN_ROCK),
    WATER(EtpetsEntity.DESCRIPTOR_ID_TERRAIN_WATER);

    private final String descriptorId;

    TerrainConstant(String descriptorId) {
        this.descriptorId = descriptorId;
    }

    @Override
    public String descriptorId() {
        return descriptorId;
    }

    @Override
    public boolean isWalkable() {
        return this == GROUND;
    }

}

