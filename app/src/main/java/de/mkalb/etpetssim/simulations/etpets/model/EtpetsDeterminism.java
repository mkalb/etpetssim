package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

public final class EtpetsDeterminism {

    private EtpetsDeterminism() {
    }

    public static int compareCoordinates(GridCoordinate left, GridCoordinate right) {
        int xCompare = Integer.compare(left.x(), right.x());
        if (xCompare != 0) {
            return xCompare;
        }
        return Integer.compare(left.y(), right.y());
    }

}
