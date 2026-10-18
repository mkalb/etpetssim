package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

/**
 * Terrain cell states for the ET Pets simulation.
 *
 * <p>Represents static terrain elements: ground, rock, and water; only ground is walkable.
 */
public enum TerrainConstant implements TerrainEntity, ConstantGridEntity {

    GROUND(EtpetsEntity.DESCRIPTOR_ID_GROUND),
    ROCK(EtpetsEntity.DESCRIPTOR_ID_ROCK),
    WATER(EtpetsEntity.DESCRIPTOR_ID_WATER);

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
