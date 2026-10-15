package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticsExtremaModeTest {

    @Test
    void testEnumValues() {
        assertNotNull(StatisticsExtremaMode.valueOf("NONE"));
        assertNotNull(StatisticsExtremaMode.valueOf("MIN"));
        assertNotNull(StatisticsExtremaMode.valueOf("MAX"));
        assertNotNull(StatisticsExtremaMode.valueOf("MIN_AND_MAX"));
    }

    @Test
    void testEnumCount() {
        assertEquals(4, StatisticsExtremaMode.values().length, "There should be exactly 4 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new StatisticsExtremaMode[]{
                        StatisticsExtremaMode.NONE,
                        StatisticsExtremaMode.MIN,
                        StatisticsExtremaMode.MAX,
                        StatisticsExtremaMode.MIN_AND_MAX
                },
                StatisticsExtremaMode.values()
        );
    }

    @Test
    void testValueOfRejectsInvalidName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> StatisticsExtremaMode.valueOf("INVALID")
        );
        assertTrue(exception.getMessage().contains("INVALID"));
    }

    @Test
    void testValueOfRejectsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> StatisticsExtremaMode.valueOf(null)
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    void testIncludesMinimum() {
        assertAll(
                () -> assertFalse(StatisticsExtremaMode.NONE.includesMinimum()),
                () -> assertTrue(StatisticsExtremaMode.MIN.includesMinimum()),
                () -> assertFalse(StatisticsExtremaMode.MAX.includesMinimum()),
                () -> assertTrue(StatisticsExtremaMode.MIN_AND_MAX.includesMinimum())
        );
    }

    @Test
    void testIncludesMaximum() {
        assertAll(
                () -> assertFalse(StatisticsExtremaMode.NONE.includesMaximum()),
                () -> assertFalse(StatisticsExtremaMode.MIN.includesMaximum()),
                () -> assertTrue(StatisticsExtremaMode.MAX.includesMaximum()),
                () -> assertTrue(StatisticsExtremaMode.MIN_AND_MAX.includesMaximum())
        );
    }

}
