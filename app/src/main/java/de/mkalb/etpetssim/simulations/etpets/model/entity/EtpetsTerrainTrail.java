package de.mkalb.etpetssim.simulations.etpets.model.entity;

public final class EtpetsTerrainTrail implements EtpetsTerrainEntity {

    private double intensity;

    public EtpetsTerrainTrail(double intensity) {
        this.intensity = Math.max(0.0d, intensity);
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_TERRAIN_TRAIL;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    public double intensity() {
        return intensity;
    }

    public void increase(double amount, double max) {
        intensity = Math.min(Math.max(0.0d, max), intensity + amount);
    }

    public void decay(double amount) {
        intensity = Math.max(0.0d, intensity - amount);
    }

    public boolean isDepleted() {
        return intensity <= 0.0d;
    }

    @Override
    public String toDisplayString() {
        return String.format("[TRAIL %.2f]", intensity);
    }

    @Override
    public String toString() {
        return "EtpetsTerrainTrail{" +
                "intensity=" + intensity +
                '}';
    }

}

