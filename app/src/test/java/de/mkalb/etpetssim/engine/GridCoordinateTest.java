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
        assertFalse(new GridCoordinate(Integer.MAX_VALUE, Integer.MAX_VALUE).isIllegal());
        assertTrue(new GridCoordinate(-1, 0).isIllegal());
        assertTrue(new GridCoordinate(0, -1).isIllegal());
        assertTrue(GridCoordinate.ILLEGAL.isIllegal());
        assertTrue(new GridCoordinate(Integer.MAX_VALUE, Integer.MIN_VALUE).isIllegal());
        assertTrue(new GridCoordinate(Integer.MIN_VALUE, Integer.MAX_VALUE).isIllegal());
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
    void testBooleanEvenOddChecks() {
        assertTrue(new GridCoordinate(0, 0).isEvenColumn());
        assertFalse(new GridCoordinate(0, 0).isOddColumn());
        assertTrue(new GridCoordinate(0, 0).isEvenRow());
        assertFalse(new GridCoordinate(0, 0).isOddRow());

        assertTrue(new GridCoordinate(2, 3).isEvenColumn());
        assertFalse(new GridCoordinate(2, 3).isOddColumn());
        assertFalse(new GridCoordinate(2, 3).isEvenRow());
        assertTrue(new GridCoordinate(2, 3).isOddRow());

        assertFalse(new GridCoordinate(1, 4).isEvenColumn());
        assertTrue(new GridCoordinate(1, 4).isOddColumn());
        assertTrue(new GridCoordinate(1, 4).isEvenRow());
        assertFalse(new GridCoordinate(1, 4).isOddRow());

        assertTrue(new GridCoordinate(6, 8).isEvenColumn());
        assertFalse(new GridCoordinate(6, 8).isOddColumn());
        assertTrue(new GridCoordinate(6, 8).isEvenRow());
        assertFalse(new GridCoordinate(6, 8).isOddRow());

        assertFalse(new GridCoordinate(7, 9).isEvenColumn());
        assertTrue(new GridCoordinate(7, 9).isOddColumn());
        assertFalse(new GridCoordinate(7, 9).isEvenRow());
        assertTrue(new GridCoordinate(7, 9).isOddRow());
    }

    @Test
    void testIsTriangleCellPointingDown() {
        assertTrue(new GridCoordinate(0, 0).isTriangleCellPointingDown());
        assertTrue(new GridCoordinate(1, 0).isTriangleCellPointingDown());
        assertFalse(new GridCoordinate(2, 1).isTriangleCellPointingDown());
        assertFalse(new GridCoordinate(3, 1).isTriangleCellPointingDown());
        assertTrue(new GridCoordinate(4, 2).isTriangleCellPointingDown());
        assertTrue(new GridCoordinate(5, 2).isTriangleCellPointingDown());
        assertFalse(new GridCoordinate(6, 3).isTriangleCellPointingDown());
        assertFalse(new GridCoordinate(7, 3).isTriangleCellPointingDown());
    }

    @Test
    void testTriangleRow() {
        assertEquals(0, new GridCoordinate(0, 0).triangleRow());
        assertEquals(0, new GridCoordinate(1, 0).triangleRow());
        assertEquals(0, new GridCoordinate(2, 1).triangleRow());
        assertEquals(0, new GridCoordinate(3, 1).triangleRow());
        assertEquals(1, new GridCoordinate(4, 2).triangleRow());
        assertEquals(1, new GridCoordinate(5, 2).triangleRow());
        assertEquals(1, new GridCoordinate(6, 3).triangleRow());
        assertEquals(1, new GridCoordinate(7, 3).triangleRow());
        assertEquals(2, new GridCoordinate(8, 4).triangleRow());
        assertEquals(2, new GridCoordinate(9, 4).triangleRow());
    }

    @Test
    void testHasTriangleCellXOffset() {
        assertFalse(new GridCoordinate(0, 0).hasTriangleCellXOffset());
        assertFalse(new GridCoordinate(1, 0).hasTriangleCellXOffset());
        assertTrue(new GridCoordinate(2, 1).hasTriangleCellXOffset());
        assertTrue(new GridCoordinate(3, 1).hasTriangleCellXOffset());
        assertTrue(new GridCoordinate(4, 2).hasTriangleCellXOffset());
        assertTrue(new GridCoordinate(5, 2).hasTriangleCellXOffset());
        assertFalse(new GridCoordinate(6, 3).hasTriangleCellXOffset());
        assertFalse(new GridCoordinate(7, 3).hasTriangleCellXOffset());
    }

    @Test
    void testHasHexagonCellYOffset() {
        assertFalse(new GridCoordinate(0, 0).hasHexagonCellYOffset());
        assertFalse(new GridCoordinate(0, 1).hasHexagonCellYOffset());
        assertTrue(new GridCoordinate(1, 2).hasHexagonCellYOffset());
        assertTrue(new GridCoordinate(1, 3).hasHexagonCellYOffset());
        assertFalse(new GridCoordinate(2, 4).hasHexagonCellYOffset());
        assertFalse(new GridCoordinate(2, 5).hasHexagonCellYOffset());
        assertTrue(new GridCoordinate(3, 6).hasHexagonCellYOffset());
        assertTrue(new GridCoordinate(3, 7).hasHexagonCellYOffset());
    }

    @Test
    void testToDisplayString() {
        assertEquals("(7, 8)", new GridCoordinate(7, 8).toDisplayString());
        assertEquals("(2048, -1024)", new GridCoordinate(2_048, -1_024).toDisplayString());
        assertEquals("(-2048, 1024)", new GridCoordinate(-2_048, 1_024).toDisplayString());
        assertEquals("(illegal)", GridCoordinate.ILLEGAL.toDisplayString());
        assertEquals("(-2147483648, -2147483648)", new GridCoordinate(Integer.MIN_VALUE, Integer.MIN_VALUE).toDisplayString());
        assertEquals("(2147483647, 2147483647)",
                new GridCoordinate(Integer.MAX_VALUE, Integer.MAX_VALUE).toDisplayString());
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