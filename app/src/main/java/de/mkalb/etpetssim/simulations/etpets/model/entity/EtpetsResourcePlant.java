package de.mkalb.etpetssim.simulations.etpets.model.entity;

public final class EtpetsResourcePlant implements EtpetsResourceEntity {

    public static final int CONSUMPTION_PER_ACT = 2;
    public static final int ENERGY_GAIN_PER_ACT = 3;

    private final double maxAmount;
    private final double regenerationPerStep;
    private double currentAmount;

    public EtpetsResourcePlant(double currentAmount, double maxAmount, double regenerationPerStep) {
        this.currentAmount = currentAmount;
        this.maxAmount = maxAmount;
        this.regenerationPerStep = regenerationPerStep;
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_RESOURCE_PLANT;
    }

    @Override
    public boolean isNone() {
        return false;
    }

    public double currentAmount() {
        return currentAmount;
    }

    public double maxAmount() {
        return maxAmount;
    }

    public boolean canConsume() {
        return currentAmount >= CONSUMPTION_PER_ACT;
    }

    public void consume() {
        currentAmount = Math.max(0.0d, currentAmount - CONSUMPTION_PER_ACT);
    }

    public void regenerate() {
        currentAmount = Math.min(maxAmount, currentAmount + regenerationPerStep);
    }

    @Override
    public String toDisplayString() {
        return String.format("[PLANT %.1f/%.1f]", currentAmount, maxAmount);
    }

    @Override
    public String toString() {
        return "EtpetsResourcePlant{" +
                "maxAmount=" + maxAmount +
                ", currentAmount=" + currentAmount +
                '}';
    }

}
