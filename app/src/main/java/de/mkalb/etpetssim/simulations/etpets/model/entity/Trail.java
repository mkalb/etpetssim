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

    public void adjustIntensity(int delta) {
        intensity = intensity + delta; // Can be lower than MIN, but will be removed in that case
        intensity = Math.min(intensity, EtpetsBalance.TRAIL_INTENSITY_RANGE_MAX);
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
