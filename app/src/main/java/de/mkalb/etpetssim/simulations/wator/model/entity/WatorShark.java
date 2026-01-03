package de.mkalb.etpetssim.simulations.wator.model.entity;

public final class WatorShark extends WatorCreature {

    private int currentEnergy;

    public WatorShark(long sequenceId, int stepIndexOfBirth, int initialEnergy) {
        super(WatorEntity.DESCRIPTOR_ID_SHARK, sequenceId, stepIndexOfBirth);
        currentEnergy = initialEnergy;
    }

    public int currentEnergy() {
        return currentEnergy;
    }

    public void reduceEnergy(int loss) {
        currentEnergy = currentEnergy - loss;
    }

    public void gainEnergy(int gain) {
        currentEnergy = currentEnergy + gain;
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
        return String.format("[SHARK #%d *%d E=%d]", sequenceId(), stepIndexOfBirth(), currentEnergy);
    }

    @Override
    public String toString() {
        return "WatorShark{" +
                "sequenceId=" + sequenceId() +
                ", stepIndexOfBirth=" + stepIndexOfBirth() +
                ", currentEnergy=" + currentEnergy +
                '}';
    }

}
