package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class RadiusRingCellTest {

    @Test
    void testRecord() {
        SortedSet<GridCoordinate> mutablePredecessors = new TreeSet<>();
        mutablePredecessors.add(new GridCoordinate(2, 0));
        mutablePredecessors.add(new GridCoordinate(1, 0));

        RadiusRingCell<String> cell = new RadiusRingCell<>(1, new GridCoordinate(2, 1), "value", mutablePredecessors);

        mutablePredecessors.add(new GridCoordinate(0, 0));

        assertAll(
                () -> assertEquals(1, cell.ring()),
                () -> assertEquals(new GridCoordinate(2, 1), cell.coordinate()),
                () -> assertEquals("value", cell.cell()),
                () -> assertEquals(List.of(new GridCoordinate(1, 0), new GridCoordinate(2, 0)),
                        new ArrayList<>(cell.reachedFromPreviousRing())),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> cell.reachedFromPreviousRing().add(new GridCoordinate(3, 0)))
        );
    }

    @Test
    void testConstructorRejectsNegativeRing() {
        assertThrows(IllegalArgumentException.class,
                () -> new RadiusRingCell<>(-1, new GridCoordinate(0, 0), "value", new TreeSet<>()));
    }

    @Test
    void testEquals() {
        GridCoordinate sharedCoordinate = new GridCoordinate(3, 3);

        RadiusRingCell<String> left = new RadiusRingCell<>(0, sharedCoordinate, "A", new TreeSet<>());
        RadiusRingCell<String> right = new RadiusRingCell<>(9, sharedCoordinate, "B", new TreeSet<>(Set.of(new GridCoordinate(3, 2))));
        RadiusRingCell<String> other = new RadiusRingCell<>(9, new GridCoordinate(4, 3), "B", new TreeSet<>());

        assertAll(
                () -> assertEquals(left, right),
                () -> assertNotEquals(left, other)
        );
    }

    @Test
    void testHashCode() {
        GridCoordinate sharedCoordinate = new GridCoordinate(3, 3);

        RadiusRingCell<String> left = new RadiusRingCell<>(0, sharedCoordinate, "A", new TreeSet<>());
        RadiusRingCell<String> right = new RadiusRingCell<>(9, sharedCoordinate, "B", new TreeSet<>(Set.of(new GridCoordinate(3, 2))));

        assertAll(
                () -> assertEquals(left.hashCode(), right.hashCode()),
                () -> assertEquals(sharedCoordinate.hashCode(), left.hashCode())
        );
    }

}

