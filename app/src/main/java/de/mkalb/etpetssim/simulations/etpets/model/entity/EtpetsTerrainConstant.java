package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum EtpetsTerrainConstant implements EtpetsTerrainEntity, ConstantGridEntity {

    GROUND(EtpetsEntity.DESCRIPTOR_ID_TERRAIN_GROUND),
    ROCK(EtpetsEntity.DESCRIPTOR_ID_TERRAIN_ROCK),
    WATER(EtpetsEntity.DESCRIPTOR_ID_TERRAIN_WATER);

    private final String descriptorId;

    EtpetsTerrainConstant(String descriptorId) {
        this.descriptorId = descriptorId;
    }

    @Override
    public String descriptorId() {
        return descriptorId;
    }

}

