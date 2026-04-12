package de.mkalb.etpetssim.simulations.etpets.model.entity;

public final class Plant extends ResourceBase {

    public static final int CONSUMPTION_PER_ACT = 2;
    public static final int ENERGY_GAIN_PER_ACT = 3;

    public Plant(double currentAmount, double maxAmount, double regenerationPerStep) {
        super(currentAmount, maxAmount, regenerationPerStep);
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_PLANT;
    }

    @Override
    protected int consumptionPerAct() {
        return CONSUMPTION_PER_ACT;
    }

    @Override
    public int energyGainPerAct() {
        return ENERGY_GAIN_PER_ACT;
    }

    @Override
    public String toDisplayString() {
        return String.format("[PLANT A=%.1f/%.1f G=%d]", currentAmount(), maxAmount(), ENERGY_GAIN_PER_ACT);
    }

    @Override
    public String toString() {
        return "Plant{" +
                "currentAmount=" + currentAmount() +
                ", maxAmount=" + maxAmount() +
                ", consumptionPerAct=" + CONSUMPTION_PER_ACT +
                ", energyGainPerAct=" + ENERGY_GAIN_PER_ACT +
                '}';
    }

}
