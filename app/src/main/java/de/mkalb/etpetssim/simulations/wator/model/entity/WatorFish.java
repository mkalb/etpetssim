package de.mkalb.etpetssim.simulations.wator.model.entity;

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

    @Override
    public String toDisplayString() {
        return String.format("[FISH #%d *%d]", sequenceId(), stepIndexOfBirth());
    }

    @Override
    public String toString() {
        return "WatorFish{" +
                "sequenceId=" + sequenceId() +
                ", stepIndexOfBirth=" + stepIndexOfBirth() +
                '}';
    }

}
