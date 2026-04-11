package de.mkalb.etpetssim.simulations.wator.model.entity;

public final class CreatureFactory {

    private long sequence;

    public CreatureFactory() {
        sequence = 0;
    }

    public Fish createFish(int stepIndexOfBirth) {
        Fish fish = new Fish(sequence, stepIndexOfBirth);
        sequence++;
        return fish;
    }

    public Shark createShark(int stepIndexOfBirth, int birthEnergy) {
        Shark shark = new Shark(sequence, stepIndexOfBirth, birthEnergy);
        sequence++;
        return shark;
    }

    public long currentSequence() {
        return sequence;
    }

}
