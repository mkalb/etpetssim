package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

import java.util.*;

public final class Insect extends ResourceBase {

    public Insect(double currentAmount, double maxAmount, double regenerationPerStep) {
        super(currentAmount, maxAmount, regenerationPerStep);
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_INSECT;
    }

    @Override
    protected int consumptionPerAct() {
        return EtpetsBalance.INSECT_CONSUMPTION_PER_ACT;
    }

    @Override
    public int energyGainPerAct() {
        return EtpetsBalance.INSECT_ENERGY_GAIN_PER_ACT;
    }

    @Override
    public double minAmount() {
        return EtpetsBalance.INSECT_CURRENT_AMOUNT_MIN;
    }

    @Override
    public String toDisplayString() {
        return String.format(Locale.ROOT,
                "[INSECT A=%.1f/%.1f R=%.2f]",
                currentAmount(),
                maxAmount(),
                regenerationPerStep());
    }

    @Override
    public String toString() {
        return "Insect{" +
                "currentAmount=" + currentAmount() +
                ", maxAmount=" + maxAmount() +
                ", regenerationPerStep=" + regenerationPerStep() +
                '}';
    }

}
