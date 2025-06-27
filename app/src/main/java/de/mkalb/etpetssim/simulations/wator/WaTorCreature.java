package de.mkalb.etpetssim.simulations.wator;

import java.util.*;

public sealed interface WaTorCreature extends Comparable<WaTorCreature>
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
    default int compareTo(WaTorCreature o) {
        return Long.compare(sequenceId(), o.sequenceId());
    }

    int numberOfReproductions();

    OptionalLong timeOfLastReproduction();

    void reproduce(WaTorCreature child);

}
