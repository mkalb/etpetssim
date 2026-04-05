package de.mkalb.etpetssim.simulations.etpets.model.entity;

public final class EtpetsResourcePlant extends EtpetsResourceGeneric {

    public static final int CONSUMPTION_PER_ACT = 2;
    public static final int ENERGY_GAIN_PER_ACT = 3;

    public EtpetsResourcePlant(double currentAmount, double maxAmount, double regenerationPerStep) {
        super(currentAmount, maxAmount, regenerationPerStep);
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_RESOURCE_PLANT;
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
        return String.format("[PLANT %.1f/%.1f]", currentAmount(), maxAmount());
    }

}
