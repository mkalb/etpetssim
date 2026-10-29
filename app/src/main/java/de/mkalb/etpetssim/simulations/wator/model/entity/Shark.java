package de.mkalb.etpetssim.simulations.wator.model.entity;

import java.util.Locale;

public final class Shark extends CreatureBase {

    private int currentEnergy;

    public Shark(long sequenceId, int stepIndexOfBirth, int initialEnergy) {
        super(WatorEntity.DESCRIPTOR_ID_SHARK, sequenceId, stepIndexOfBirth);
        currentEnergy = initialEnergy;
    }

    public int currentEnergy() {
        return currentEnergy;
    }

    public void adjustEnergy(int delta) {
        currentEnergy = currentEnergy + delta;
    }

    @Override
    public boolean isFish() {
        return false;
    }

    @Override
    public boolean isShark() {
        return true;
    }

    @Override
    public String toDisplayString() {
        return String.format(Locale.ROOT, "[SHARK #%d *%d E=%d]",
                sequenceId(),
                stepIndexOfBirth(),
                currentEnergy);
    }

    @Override
    public String toString() {
        return "Shark{" +
                "sequenceId=" + sequenceId() +
                ", stepIndexOfBirth=" + stepIndexOfBirth() +
                ", reproductionCount=" + reproductionCount() +
                ", stepIndexOfLastReproduction=" + stepIndexOfLastReproduction() +
                ", currentEnergy=" + currentEnergy +
                '}';
    }

}
