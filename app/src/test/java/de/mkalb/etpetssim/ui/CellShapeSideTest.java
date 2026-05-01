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
    void testDeclarationOrder() {
        assertArrayEquals(
                new CellShapeSide[]{CellShapeSide.TOP, CellShapeSide.BOTTOM, CellShapeSide.LEFT, CellShapeSide.RIGHT},
                CellShapeSide.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> CellShapeSide.valueOf("INVALID"));
    }

}
