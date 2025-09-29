package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class GridCoordinateTest {

    @Test
    void testRecord() {
        GridCoordinate coordinate = new GridCoordinate(5, 10);
        assertEquals(5, coordinate.x());
        assertEquals(10, coordinate.y());

        assertTrue(coordinate.toString().contains("x=5"));
        assertTrue(coordinate.toString().contains("y=10"));
    }

    @Test
    void testConstants() {
        assertEquals(0, GridCoordinate.ORIGIN.x());
        assertEquals(0, GridCoordinate.ORIGIN.y());
        assertTrue(GridCoordinate.ILLEGAL.isIllegal());
        assertEquals(Integer.MIN_VALUE, GridCoordinate.ILLEGAL.x());
        assertEquals(Integer.MIN_VALUE, GridCoordinate.ILLEGAL.y());
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
        GridCoordinate coordinate = new GridCoordinate(5, 5);
        assertTrue(coordinate.isWithinBounds(0, 0, 10, 10));
        assertFalse(coordinate.isWithinBounds(0, 0, 5, 5));
        assertFalse(coordinate.isWithinBounds(6, 6, 10, 10));
    }

    @Test
    void testIsWithinBounds() {
        GridCoordinate coordinate = new GridCoordinate(5, 5);
        assertTrue(coordinate.isWithinBounds(new GridCoordinate(0, 0), new GridCoordinate(10, 10)));
        assertFalse(coordinate.isWithinBounds(new GridCoordinate(0, 0), new GridCoordinate(5, 5)));
        assertFalse(coordinate.isWithinBounds(new GridCoordinate(6, 6), new GridCoordinate(10, 10)));
    }

    @Test
    void testIsWithinOriginBounds() {
        GridCoordinate coordinate = new GridCoordinate(5, 5);
        assertTrue(coordinate.isWithinOriginBounds(new GridCoordinate(10, 10)));
        assertFalse(coordinate.isWithinOriginBounds(new GridCoordinate(5, 5)));
        assertFalse(GridCoordinate.ILLEGAL.isWithinOriginBounds(new GridCoordinate(5, 5)));
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
        assertFalse(new GridCoordinate(1, 0).isTriangleCellPointingDown());
        assertFalse(new GridCoordinate(2, 1).isTriangleCellPointingDown());
        assertTrue(new GridCoordinate(3, 1).isTriangleCellPointingDown());
        assertTrue(new GridCoordinate(4, 2).isTriangleCellPointingDown());
        assertFalse(new GridCoordinate(5, 2).isTriangleCellPointingDown());
        assertFalse(new GridCoordinate(6, 3).isTriangleCellPointingDown());
        assertTrue(new GridCoordinate(7, 3).isTriangleCellPointingDown());
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
    void testClampToBoundsInt() {
        GridCoordinate coordinate = new GridCoordinate(15, -5);
        GridCoordinate clamped = coordinate.clampToBounds(0, 0, 10, 10);
        assertEquals(new GridCoordinate(9, 0), clamped);
    }

    @Test
    void testClampToBounds() {
        GridCoordinate coordinate = new GridCoordinate(15, -5);
        GridCoordinate clamped = coordinate.clampToBounds(new GridCoordinate(0, 0), new GridCoordinate(10, 10));
        assertEquals(new GridCoordinate(9, 0), clamped);
    }

    @Test
    void testClampToOriginBounds() {
        GridCoordinate coordinate = new GridCoordinate(15, -5);
        assertEquals(new GridCoordinate(9, 0), coordinate.clampToOriginBounds(new GridCoordinate(10, 10)));
        assertEquals(new GridCoordinate(0, 0), GridCoordinate.ILLEGAL.clampToOriginBounds(new GridCoordinate(10, 10)));
    }

    @Test
    void testIncremented() {
        GridCoordinate coordinate = new GridCoordinate(3, 4);
        assertEquals(new GridCoordinate(4, 5), coordinate.incremented());
    }

    @Test
    void testDecremented() {
        GridCoordinate coordinate = new GridCoordinate(3, 4);
        assertEquals(new GridCoordinate(2, 3), coordinate.decremented());
    }

    @Test
    void testOffsetInt() {
        GridCoordinate coordinate = new GridCoordinate(3, 4);
        assertEquals(new GridCoordinate(5, 1), coordinate.offset(2, -3));
        assertEquals(new GridCoordinate(1, 7), coordinate.offset(-2, 3));
    }

    @Test
    void testOffset() {
        GridCoordinate coordinate = new GridCoordinate(3, 4);
        assertEquals(new GridCoordinate(5, 1), coordinate.offset(new GridOffset(2, -3)));
        assertEquals(new GridCoordinate(1, 7), coordinate.offset(new GridOffset(-2, 3)));
    }

    @Test
    void testOffsetTo() {
        GridCoordinate coordinate = new GridCoordinate(3, 4);
        assertEquals(new GridOffset(-1, 3), coordinate.offsetTo(new GridCoordinate(2, 7)));
        assertEquals(new GridOffset(-3, -4), coordinate.offsetTo(new GridCoordinate(0, 0)));
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

}
