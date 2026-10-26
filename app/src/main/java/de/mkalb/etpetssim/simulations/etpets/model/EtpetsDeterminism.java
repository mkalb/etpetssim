package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

/**
 * Deterministic orderings of the ET Pets simulation, so that a run with the same seed always processes candidates in
 * the same order.
 */
public final class EtpetsDeterminism {

    private EtpetsDeterminism() {
    }

    /**
     * Compares two coordinates first by {@code x}, then by {@code y}.
     *
     * @param left  the first coordinate
     * @param right the second coordinate
     * @return a negative value, zero, or a positive value if {@code left} is less than, equal to, or greater than
     * {@code right}
     */
    public static int compareCoordinates(GridCoordinate left, GridCoordinate right) {
        int xCompare = Integer.compare(left.x(), right.x());
        if (xCompare != 0) {
            return xCompare;
        }
        return Integer.compare(left.y(), right.y());
    }

}
