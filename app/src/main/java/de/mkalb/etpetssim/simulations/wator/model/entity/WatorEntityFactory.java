package de.mkalb.etpetssim.simulations.wator.model.entity;

public final class WatorEntityFactory {

    private long sequence;

    public WatorEntityFactory() {
        sequence = 0;
    }

    public WatorFish createFish(int stepIndexOfBirth) {
        WatorFish watorFish = new WatorFish(sequence, stepIndexOfBirth);
        sequence++;
        return watorFish;
    }

    public WatorShark createShark(int stepIndexOfBirth, int birthEnergy) {
        WatorShark shark = new WatorShark(sequence, stepIndexOfBirth, birthEnergy);
        sequence++;
        return shark;
    }

    public long currentSequence() {
        return sequence;
    }

}
