package de.mkalb.etpetssim.simulations.wator;

import java.util.*;

public final class WaTorFish implements WaTorCreature {

    private final long sequenceId;
    private final long timeOfBirth;
    private final WaTorCoordinate placeOfBirth;
    private final List<Long> timeOfReproduction;
    private WaTorCoordinate currentPlace;

    public WaTorFish(long sequenceId, long timeOfBirth, WaTorCoordinate placeOfBirth) {
        this.sequenceId = sequenceId;
        this.timeOfBirth = timeOfBirth;
        this.placeOfBirth = placeOfBirth;
        currentPlace = placeOfBirth;
        timeOfReproduction = new ArrayList<>();
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

    @Override
    public String toString() {
        return "WaTorFish{" +
                "sequenceId=" + sequenceId +
                ", timeOfBirth=" + timeOfBirth +
                ", placeOfBirth=" + placeOfBirth +
                ", currentPlace=" + currentPlace +
                ", timeOfReproduction=" + timeOfReproduction +
                '}';
    }

}
