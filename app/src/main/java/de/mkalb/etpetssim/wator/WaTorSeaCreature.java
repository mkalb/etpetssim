package de.mkalb.etpetssim.wator;

public sealed interface WaTorSeaCreature extends Comparable<WaTorSeaCreature>
        permits WaTorFish, WaTorShark {

    long sequenceId();

    long timeOfBirth();

    WaTorCoordinate placeOfBirth();

    WaTorCoordinate currentPlace();

    void moveTo(WaTorCoordinate newCoordinate);

    default long age(long timeCounter) {
        return timeCounter - timeOfBirth();
    }

    @Override
    default int compareTo(WaTorSeaCreature o) {
        return Long.compare(sequenceId(), o.sequenceId());
    }

}
