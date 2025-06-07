package de.mkalb.etpetssim.wator;

import java.util.*;

public final class WaTorShark implements WaTorSeaCreature {

    private final long sequenceId;
    private final long timeOfBirth;
    private final WaTorCoordinate placeOfBirth;
    private WaTorCoordinate currentPlace;
    private int currentEnergy;
    private final List<Long> timeOfReproduction;

    public WaTorShark(long sequenceId, long timeOfBirth, WaTorCoordinate placeOfBirth, int initialEnergy) {
        this.sequenceId = sequenceId;
        this.timeOfBirth = timeOfBirth;
        this.placeOfBirth = placeOfBirth;
        currentPlace = placeOfBirth;
        currentEnergy = initialEnergy;
        timeOfReproduction = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "WaTorShark{" +
                "sequenceId=" + sequenceId +
                ", timeOfBirth=" + timeOfBirth +
                ", placeOfBirth=" + placeOfBirth +
                ", currentPlace=" + currentPlace +
                ", currentEnergy=" + currentEnergy +
                '}';
    }

    @Override
    public long sequenceId() {
        return sequenceId;
    }

    @Override
    public long timeOfBirth() {
        return timeOfBirth;
    }

    @Override
    public WaTorCoordinate placeOfBirth() {
        return placeOfBirth;
    }

    @Override
    public WaTorCoordinate currentPlace() {
        return currentPlace;
    }

    @Override
    public void moveTo(WaTorCoordinate newCoordinate) {
        currentPlace = newCoordinate;
    }

    public int currentEnergy() {
        return currentEnergy;
    }

    public int reduceEnergy() {
        currentEnergy--;
        return currentEnergy;
    }

    public int gainEnergy(WaTorFish fish) {
        currentEnergy = currentEnergy + 5; // Gain energy from eating fish
        return currentEnergy;
    }

    public int numberOfReproductions() {
        return timeOfReproduction.size();
    }

    public OptionalLong timeOfLastReproduction() {
        return timeOfReproduction.isEmpty() ? OptionalLong.empty() : OptionalLong.of(timeOfReproduction.getLast());
    }

    public void reproduce(WaTorShark childShark) {
        timeOfReproduction.add(childShark.timeOfBirth());
    }

}
