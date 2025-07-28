package de.mkalb.etpetssim.simulations.wator.model;

public final class WatorShark extends WatorCreature {

    private int currentEnergy;

    public WatorShark(long sequenceId, long timeOfBirth, int initialEnergy) {
        super(WatorEntity.DESCRIPTOR_ID_SHARK, sequenceId, timeOfBirth);
        currentEnergy = initialEnergy;
    }

    public int currentEnergy() {
        return currentEnergy;
    }

    public int reduceEnergy(int loss) {
        currentEnergy = currentEnergy - loss;
        return currentEnergy;
    }

    public int gainEnergy(int gain) {
        currentEnergy = currentEnergy + gain;
        return currentEnergy;
    }

    @Override
    public boolean isFish() {
        return false;
    }

    @Override
    public boolean isShark() {
        return true;
    }

}
