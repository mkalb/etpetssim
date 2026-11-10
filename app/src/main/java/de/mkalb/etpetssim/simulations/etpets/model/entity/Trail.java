package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

import java.util.Locale;

/**
 * Trail of ET Pets, a walkable terrain that pets reinforce and that decays without them.
 */
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
        // The intensity may fall below the minimum; EtpetsTerrainLogic then removes the trail.
        intensity = Math.min(intensity + delta, EtpetsBalance.TRAIL_INTENSITY_MAX);
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
