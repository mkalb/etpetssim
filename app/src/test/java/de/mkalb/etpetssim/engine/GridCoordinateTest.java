package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
class GridCoordinateTest {

    @Test
    void testConstructorAndAccessors() {
        GridCoordinate gridCoordinate = new GridCoordinate(5, 10);
        assertEquals(5, gridCoordinate.x());
        assertEquals(10, gridCoordinate.y());
    }

    @Test
    void testIsIllegal() {
        assertFalse(new GridCoordinate(0, 0).isIllegal());
        assertTrue(new GridCoordinate(-1, 0).isIllegal());
        assertTrue(new GridCoordinate(0, -1).isIllegal());
        assertTrue(GridCoordinate.ILLEGAL.isIllegal());
    }

    @Test
    void testIsWithinBoundsInt() {
        GridCoordinate gridCoordinate = new GridCoordinate(5, 5);
        assertTrue(gridCoordinate.isWithinBounds(0, 0, 10, 10));
        assertFalse(gridCoordinate.isWithinBounds(0, 0, 5, 5));
        assertFalse(gridCoordinate.isWithinBounds(6, 6, 10, 10));
    }

    @Test
    void testIsWithinBoundsCoordinates() {
        GridCoordinate gridCoordinate = new GridCoordinate(5, 5);
        assertTrue(gridCoordinate.isWithinBounds(new GridCoordinate(0, 0), new GridCoordinate(10, 10)));
        assertFalse(gridCoordinate.isWithinBounds(new GridCoordinate(0, 0), new GridCoordinate(5, 5)));
        assertFalse(gridCoordinate.isWithinBounds(new GridCoordinate(6, 6), new GridCoordinate(10, 10)));
    }

    @Test
    void testIsWithinOriginBounds() {
        GridCoordinate gridCoordinate = new GridCoordinate(5, 5);
        assertTrue(gridCoordinate.isWithinOriginBounds(new GridCoordinate(10, 10)));
        assertFalse(gridCoordinate.isWithinOriginBounds(new GridCoordinate(5, 5)));
        assertFalse(GridCoordinate.ILLEGAL.isWithinOriginBounds(new GridCoordinate(5, 5)));
    }

    @Test
    void testClampToBoundsInt() {
        GridCoordinate gridCoordinate = new GridCoordinate(15, -5);
        GridCoordinate clamped = gridCoordinate.clampToBounds(0, 0, 10, 10);
        assertEquals(new GridCoordinate(9, 0), clamped);
    }

    @Test
    void testClampToBoundsCoordinates() {
        GridCoordinate gridCoordinate = new GridCoordinate(15, -5);
        GridCoordinate clamped = gridCoordinate.clampToBounds(new GridCoordinate(0, 0), new GridCoordinate(10, 10));
        assertEquals(new GridCoordinate(9, 0), clamped);
    }

    @Test
    void testClampToOriginBounds() {
        GridCoordinate gridCoordinate = new GridCoordinate(15, -5);
        assertEquals(new GridCoordinate(9, 0), gridCoordinate.clampToOriginBounds(new GridCoordinate(10, 10)));
        assertEquals(new GridCoordinate(0, 0), GridCoordinate.ILLEGAL.clampToOriginBounds(new GridCoordinate(10, 10)));
    }

    @Test
    void testIncremented() {
        GridCoordinate gridCoordinate = new GridCoordinate(3, 4);
        assertEquals(new GridCoordinate(4, 5), gridCoordinate.incremented());
    }

    @Test
    void testDecremented() {
        GridCoordinate gridCoordinate = new GridCoordinate(3, 4);
        assertEquals(new GridCoordinate(2, 3), gridCoordinate.decremented());
    }

    @Test
    void testOffset() {
        GridCoordinate gridCoordinate = new GridCoordinate(3, 4);
        assertEquals(new GridCoordinate(5, 1), gridCoordinate.offset(2, -3));
        assertEquals(new GridCoordinate(1, 7), gridCoordinate.offset(-2, 3));
    }

    @Test
    void testAsString() {
        assertEquals("(7, 8)", new GridCoordinate(7, 8).asString());
        assertEquals("(2048, -1024)", new GridCoordinate(2_048, -1_024).asString());
    }

    @Test
    void testConstants() {
        assertEquals(0, GridCoordinate.ORIGIN.x());
        assertEquals(0, GridCoordinate.ORIGIN.y());
        assertTrue(GridCoordinate.ILLEGAL.isIllegal());
        assertEquals(Integer.MIN_VALUE, GridCoordinate.ILLEGAL.x());
        assertEquals(Integer.MIN_VALUE, GridCoordinate.ILLEGAL.y());
    }

}