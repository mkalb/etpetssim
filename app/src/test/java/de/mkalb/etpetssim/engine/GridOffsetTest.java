package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class GridOffsetTest {

    @Test
    void testRecord() {
        GridOffset offset = new GridOffset(3, -5);
        assertEquals(3, offset.dx());
        assertEquals(-5, offset.dy());

        assertTrue(offset.toString().contains("dx=3"));
        assertTrue(offset.toString().contains("dy=-5"));

        GridOffset zero = new GridOffset(0, 0);
        assertEquals(0, zero.dx());
        assertEquals(0, zero.dy());

        GridOffset negative = new GridOffset(-100, -200);
        assertEquals(-100, negative.dx());
        assertEquals(-200, negative.dy());
    }

    @Test
    void testStaticBetween() {
        assertEquals(new GridOffset(0, 9), GridOffset.between(new GridCoordinate(0, 0), new GridCoordinate(0, 9)));
        assertEquals(new GridOffset(3, 4), GridOffset.between(new GridCoordinate(1, 2), new GridCoordinate(4, 6)));
    }

    @Test
    void testAdd() {
        assertEquals(new GridOffset(6, 2), new GridOffset(2, 3).add(new GridOffset(4, -1)));
    }

    @Test
    void testSubtract() {
        assertEquals(new GridOffset(3, 4), new GridOffset(5, 7).subtract(new GridOffset(2, 3)));
    }

    @Test
    void testNegate() {
        assertEquals(new GridOffset(0, 0), new GridOffset(0, 0).negate());
        assertEquals(new GridOffset(-3, 4), new GridOffset(3, -4).negate());
    }

    @Test
    void testScale() {
        assertEquals(new GridOffset(0, 0), new GridOffset(0, 0).scale(2));
        assertEquals(new GridOffset(4, -6), new GridOffset(2, -3).scale(2));
        assertEquals(new GridOffset(-2, 3), new GridOffset(2, -3).scale(-1));
    }

    @Test
    void testIsZero() {
        assertTrue(new GridOffset(0, 0).isZero());
        assertFalse(new GridOffset(1, 0).isZero());
        assertFalse(new GridOffset(0, 1).isZero());
    }

    @Test
    void testManhattanLength() {
        assertEquals(0, new GridOffset(0, 0).manhattanLength());
        assertEquals(9, new GridOffset(9, 0).manhattanLength());
        assertEquals(9, new GridOffset(0, 9).manhattanLength());
        assertEquals(5, new GridOffset(2, 3).manhattanLength());
        assertEquals(7, new GridOffset(-3, 4).manhattanLength());
    }

    @SuppressWarnings("InsertLiteralUnderscores")
    @Test
    void testEuclideanLength() {
        assertEquals(5.0, new GridOffset(3, 4).euclideanLength(), 0.00001);
        assertEquals(Math.sqrt(13), new GridOffset(2, 3).euclideanLength(), 0.00001);
    }

    @Test
    void testToDisplayString() {
        assertEquals("[+2, -3]", new GridOffset(2, -3).toDisplayString());
        assertEquals("[+0, +0]", new GridOffset(0, 0).toDisplayString());
    }

}
