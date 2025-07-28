package de.mkalb.etpetssim.simulations.wator.model;

public final class WatorEntityFactory {

    private long sequence;

    public WatorEntityFactory() {
        sequence = 0;
    }

    public WatorFish createFish(long timeOfBirth) {
        WatorFish watorFish = new WatorFish(sequence, timeOfBirth);
        sequence++;
        return watorFish;
    }

    public WatorShark createShark(long timeOfBirth, int birthEnergy) {
        WatorShark shark = new WatorShark(sequence, timeOfBirth, birthEnergy);
        sequence++;
        return shark;
    }

    public long currentSequence() {
        return sequence;
    }

}
