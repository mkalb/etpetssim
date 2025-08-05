package de.mkalb.etpetssim.simulations.wator.model;

public final class WatorFish extends WatorCreature {

    public WatorFish(long sequenceId, int stepIndexOfBirth) {
        super(WatorEntity.DESCRIPTOR_ID_FISH, sequenceId, stepIndexOfBirth);
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
