package de.mkalb.etpetssim.simulations.wator.model;

public final class WatorFish extends WatorCreature {

    WatorFish(long sequenceId, long timeOfBirth) {
        super(WatorEntity.DESCRIPTOR_ID_FISH, sequenceId, timeOfBirth);
    }

    @Override
    public boolean isFish() {
        return true;
    }

    @Override
    public boolean isShark() {
        return false;
    }

}
