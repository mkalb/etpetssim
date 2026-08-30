package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticExtremaModeTest {

    @Test
    void testEnumValues() {
        assertNotNull(StatisticExtremaMode.valueOf("NONE"));
        assertNotNull(StatisticExtremaMode.valueOf("MIN"));
        assertNotNull(StatisticExtremaMode.valueOf("MAX"));
        assertNotNull(StatisticExtremaMode.valueOf("MIN_AND_MAX"));
    }

    @Test
    void testEnumCount() {
        assertEquals(4, StatisticExtremaMode.values().length, "There should be exactly 4 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new StatisticExtremaMode[]{
                        StatisticExtremaMode.NONE,
                        StatisticExtremaMode.MIN,
                        StatisticExtremaMode.MAX,
                        StatisticExtremaMode.MIN_AND_MAX
                },
                StatisticExtremaMode.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> StatisticExtremaMode.valueOf("INVALID")
        );
        assertTrue(exception.getMessage().contains("INVALID"));
    }

    @Test
    void testValueOfNullThrows() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> StatisticExtremaMode.valueOf(null)
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    void testTracksMinimum() {
        assertAll(
                () -> assertFalse(StatisticExtremaMode.NONE.tracksMinimum()),
                () -> assertTrue(StatisticExtremaMode.MIN.tracksMinimum()),
                () -> assertFalse(StatisticExtremaMode.MAX.tracksMinimum()),
                () -> assertTrue(StatisticExtremaMode.MIN_AND_MAX.tracksMinimum())
        );
    }

    @Test
    void testTracksMaximum() {
        assertAll(
                () -> assertFalse(StatisticExtremaMode.NONE.tracksMaximum()),
                () -> assertFalse(StatisticExtremaMode.MIN.tracksMaximum()),
                () -> assertTrue(StatisticExtremaMode.MAX.tracksMaximum()),
                () -> assertTrue(StatisticExtremaMode.MIN_AND_MAX.tracksMaximum())
        );
    }

}
