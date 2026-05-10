package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class RadiusRingCellTest {

    @Test
    void testConstructorCreatesSortedUnmodifiableCopy() {
        SortedSet<GridCoordinate> mutablePredecessors = new TreeSet<>();
        mutablePredecessors.add(new GridCoordinate(2, 0));
        mutablePredecessors.add(new GridCoordinate(1, 0));

        RadiusRingCell<String> cell = new RadiusRingCell<>(1, new GridCoordinate(2, 1), "value", mutablePredecessors);

        mutablePredecessors.add(new GridCoordinate(0, 0));

        assertEquals(List.of(new GridCoordinate(1, 0), new GridCoordinate(2, 0)),
                new ArrayList<>(cell.reachedFromPreviousRing()));
        assertThrows(UnsupportedOperationException.class,
                () -> cell.reachedFromPreviousRing().add(new GridCoordinate(3, 0)));
    }

    @Test
    void testEqualsAndHashCodeUseCoordinateOnly() {
        GridCoordinate sharedCoordinate = new GridCoordinate(3, 3);

        RadiusRingCell<String> left = new RadiusRingCell<>(0, sharedCoordinate, "A", new TreeSet<>());
        RadiusRingCell<String> right = new RadiusRingCell<>(9, sharedCoordinate, "B", new TreeSet<>(Set.of(new GridCoordinate(3, 2))));
        RadiusRingCell<String> other = new RadiusRingCell<>(9, new GridCoordinate(4, 3), "B", new TreeSet<>());

        assertAll(
                () -> assertEquals(left, right),
                () -> assertEquals(left.hashCode(), right.hashCode()),
                () -> assertNotEquals(left, other)
        );
    }

}

