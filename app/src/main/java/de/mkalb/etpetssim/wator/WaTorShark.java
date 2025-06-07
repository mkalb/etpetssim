package de.mkalb.etpetssim.wator;

import java.util.*;

public final class WaTorShark implements WaTorCreature {

    private final long sequenceId;
    private final long timeOfBirth;
    private final WaTorCoordinate placeOfBirth;

    private WaTorCoordinate currentPlace;
    private final List<Long> timeOfReproduction;
    private int currentEnergy;

    public WaTorShark(long sequenceId, long timeOfBirth, WaTorCoordinate placeOfBirth, int initialEnergy) {
        this.sequenceId = sequenceId;
        this.timeOfBirth = timeOfBirth;
        this.placeOfBirth = placeOfBirth;
        currentPlace = placeOfBirth;
        timeOfReproduction = new ArrayList<>();
        currentEnergy = initialEnergy;
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

    @Override
    public int numberOfReproductions() {
        return timeOfReproduction.size();
    }

    @Override
    public OptionalLong timeOfLastReproduction() {
        return timeOfReproduction.isEmpty() ? OptionalLong.empty() : OptionalLong.of(timeOfReproduction.getLast());
    }

    @Override
    public void reproduce(WaTorCreature child) {
        timeOfReproduction.add(child.timeOfBirth());
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
    public String toString() {
        return "WaTorShark{" +
                "sequenceId=" + sequenceId +
                ", timeOfBirth=" + timeOfBirth +
                ", placeOfBirth=" + placeOfBirth +
                ", currentPlace=" + currentPlace +
                ", timeOfReproduction=" + timeOfReproduction +
                ", currentEnergy=" + currentEnergy +
                '}';
    }

}
