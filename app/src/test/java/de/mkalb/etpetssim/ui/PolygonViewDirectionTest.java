package de.mkalb.etpetssim.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class PolygonViewDirectionTest {

    @Test
    void testEnumValues() {
        assertNotNull(PolygonViewDirection.valueOf("TOP"));
        assertNotNull(PolygonViewDirection.valueOf("BOTTOM"));
        assertNotNull(PolygonViewDirection.valueOf("LEFT"));
        assertNotNull(PolygonViewDirection.valueOf("RIGHT"));
    }

    @Test
    void testEnumCount() {
        assertEquals(4, PolygonViewDirection.values().length, "There should be exactly 4 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, PolygonViewDirection.TOP.ordinal());
        assertEquals(1, PolygonViewDirection.BOTTOM.ordinal());
        assertEquals(2, PolygonViewDirection.LEFT.ordinal());
        assertEquals(3, PolygonViewDirection.RIGHT.ordinal());
    }

}
