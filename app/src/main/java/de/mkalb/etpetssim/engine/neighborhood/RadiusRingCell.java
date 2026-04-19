package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;

import java.util.*;

/**
 * Represents a cell snapshot within a specific radius ring around a start coordinate.
 * <p>
 * Besides the ring index and coordinate, this record stores the generic cell value and
 * the sorted set of coordinates from the previous ring through which this cell was reached.
 * The predecessor set is always exposed as an unmodifiable sorted set.
 * <p>
 * Equality and hash code are intentionally based on the {@link #coordinate()} only.
 * This allows instances to be used as map keys when a coordinate should be treated as
 * globally unique across all radius rings.
 *
 * @param <C>                     the generic cell value type
 * @param ring                    the radius ring index of this cell (0 for the start coordinate)
 * @param coordinate              the coordinate of this cell within the grid
 * @param cell                    the generic cell value for the coordinate
 * @param reachedFromPreviousRing unmodifiable sorted set of coordinates from the previous ring
 *                                through which this cell was reached
 */
public record RadiusRingCell<C>(int ring,
                                GridCoordinate coordinate,
                                C cell,
                                SortedSet<GridCoordinate> reachedFromPreviousRing) {

    /**
     * Canonical constructor creating a defensive, sorted, unmodifiable copy of the predecessor set.
     */
    public RadiusRingCell {
        reachedFromPreviousRing = Collections.unmodifiableSortedSet(new TreeSet<>(reachedFromPreviousRing));
    }

    /**
     * Compares this cell with another object using the coordinate only.
     *
     * @param obj the object to compare with
     * @return {@code true} if both objects represent the same coordinate, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RadiusRingCell<?> that)) {
            return false;
        }
        return coordinate.equals(that.coordinate);
    }

    /**
     * Returns a hash code based on the coordinate only.
     *
     * @return the hash code of the coordinate
     */
    @Override
    public int hashCode() {
        return coordinate.hashCode();
    }

}

