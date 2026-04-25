package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

public final class Plant extends ResourceBase {

    public Plant(double currentAmount, double maxAmount, double regenerationPerStep) {
        super(currentAmount, maxAmount, regenerationPerStep);
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_PLANT;
    }

    @Override
    protected int consumptionPerAct() {
        return EtpetsBalance.PLANT_CONSUMPTION_PER_ACT;
    }

    @Override
    public int energyGainPerAct() {
        return EtpetsBalance.PLANT_ENERGY_GAIN_PER_ACT;
    }

    @Override
    public double minAmount() {
        return EtpetsBalance.PLANT_CURRENT_AMOUNT_MIN;
    }

    @Override
    public String toDisplayString() {
        return String.format("[PLANT A=%.1f/%.1f R=%f]", currentAmount(), maxAmount(), regenerationPerStep());
    }

    @Override
    public String toString() {
        return "Plant{" +
                "currentAmount=" + currentAmount() +
                ", maxAmount=" + maxAmount() +
                ", regenerationPerStep=" + regenerationPerStep() +
                '}';
    }

}
