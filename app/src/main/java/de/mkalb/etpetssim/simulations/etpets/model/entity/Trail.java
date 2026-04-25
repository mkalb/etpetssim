package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

import java.util.*;

public final class Trail implements TerrainEntity {

    private int intensity;

    public Trail(int intensity) {
        this.intensity = intensity;
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_TRAIL;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    public int intensity() {
        return intensity;
    }

    public void incrementIntensity(int amount) {
        intensity = Math.min(intensity + amount, EtpetsBalance.TRAIL_INTENSITY_MAX);
    }

    public void decrementIntensity(int amount) {
        intensity = intensity - amount; // Can be lower than MIN, but will be removed in that case
    }

    @Override
    public String toDisplayString() {
        return String.format(Locale.ROOT, "[TRAIL I=%d]",
                intensity);
    }

    @Override
    public String toString() {
        return "Trail{" +
                "intensity=" + intensity +
                '}';
    }

}
