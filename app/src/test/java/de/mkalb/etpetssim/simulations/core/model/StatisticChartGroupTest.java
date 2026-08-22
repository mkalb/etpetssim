package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticChartGroupTest {

    @Test
    void testEnumValues() {
        assertNotNull(StatisticChartGroup.valueOf("NONE"));
        assertNotNull(StatisticChartGroup.valueOf("PRIMARY"));
        assertNotNull(StatisticChartGroup.valueOf("SECONDARY"));
    }

    @Test
    void testEnumCount() {
        assertEquals(3, StatisticChartGroup.values().length, "There should be exactly 3 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new StatisticChartGroup[]{
                        StatisticChartGroup.NONE,
                        StatisticChartGroup.PRIMARY,
                        StatisticChartGroup.SECONDARY
                },
                StatisticChartGroup.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> StatisticChartGroup.valueOf("INVALID")
        );
        assertTrue(exception.getMessage().contains("INVALID"));
    }

    @Test
    void testValueOfNullThrows() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> StatisticChartGroup.valueOf(null)
        );
        assertNotNull(exception.getMessage());
    }

}
