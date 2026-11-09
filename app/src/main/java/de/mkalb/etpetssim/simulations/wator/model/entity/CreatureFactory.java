package de.mkalb.etpetssim.simulations.wator.model.entity;

/**
 * Factory of the Wa-Tor creatures with ascending sequence IDs.
 */
public final class CreatureFactory {

    private long sequence;

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

}
