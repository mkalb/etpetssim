package de.mkalb.etpetssim.simulations.etpets.model.entity;

public final class EtpetsResourceInsect extends EtpetsResourceGeneric {

    public static final int CONSUMPTION_PER_ACT = 3;
    public static final int ENERGY_GAIN_PER_ACT = 9;

    public EtpetsResourceInsect(double currentAmount, double maxAmount, double regenerationPerStep) {
        super(currentAmount, maxAmount, regenerationPerStep);
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_RESOURCE_INSECT;
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
        return String.format("[INSECT %.1f/%.1f]", currentAmount(), maxAmount());
    }

}
