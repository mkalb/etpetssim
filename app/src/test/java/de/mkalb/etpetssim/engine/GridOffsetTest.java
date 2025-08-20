package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
class GridOffsetTest {

    @Test
    void testValidConstructionAndAccessors() {
        GridOffset offset = new GridOffset(3, -5);
        assertEquals(3, offset.dx());
        assertEquals(-5, offset.dy());
    }

    @Test
    void testEqualityAndHashCode() {
        GridOffset offset1 = new GridOffset(2, 4);
        GridOffset offset2 = new GridOffset(2, 4);
        GridOffset offset3 = new GridOffset(4, 2);

        assertEquals(offset1, offset2);
        assertEquals(offset1.hashCode(), offset2.hashCode());
        assertNotEquals(offset1, offset3);
    }

    @Test
    void testToStringFormat() {
        GridOffset offset = new GridOffset(7, -2);
        assertTrue(offset.toString().contains("dx=7"));
        assertTrue(offset.toString().contains("dy=-2"));
    }

    @Test
    void testEdgeCases() {
        GridOffset zero = new GridOffset(0, 0);
        assertEquals(0, zero.dx());
        assertEquals(0, zero.dy());

        GridOffset negative = new GridOffset(-100, -200);
        assertEquals(-100, negative.dx());
        assertEquals(-200, negative.dy());
    }

}
