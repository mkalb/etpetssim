package de.mkalb.etpetssim.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class StrokeAdjustmentTest {

    @Test
    void testEnumValues() {
        assertNotNull(StrokeAdjustment.valueOf("INSIDE"));
        assertNotNull(StrokeAdjustment.valueOf("OUTSIDE"));
        assertNotNull(StrokeAdjustment.valueOf("CENTERED"));
    }

    @Test
    void testEnumCount() {
        assertEquals(3, StrokeAdjustment.values().length, "There should be exactly 3 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, StrokeAdjustment.INSIDE.ordinal());
        assertEquals(1, StrokeAdjustment.OUTSIDE.ordinal());
        assertEquals(2, StrokeAdjustment.CENTERED.ordinal());
    }

}
