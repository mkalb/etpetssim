package de.mkalb.etpetssim.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class CellShapeSideTest {

    @Test
    void testEnumValues() {
        assertNotNull(CellShapeSide.valueOf("TOP"));
        assertNotNull(CellShapeSide.valueOf("BOTTOM"));
        assertNotNull(CellShapeSide.valueOf("LEFT"));
        assertNotNull(CellShapeSide.valueOf("RIGHT"));
    }

    @Test
    void testEnumCount() {
        assertEquals(4, CellShapeSide.values().length, "There should be exactly 4 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, CellShapeSide.TOP.ordinal());
        assertEquals(1, CellShapeSide.BOTTOM.ordinal());
        assertEquals(2, CellShapeSide.LEFT.ordinal());
        assertEquals(3, CellShapeSide.RIGHT.ordinal());
    }

}
