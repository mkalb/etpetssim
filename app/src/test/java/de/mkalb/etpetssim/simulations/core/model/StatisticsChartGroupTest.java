package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticsChartGroupTest {

    @Test
    void testEnumValues() {
        assertNotNull(StatisticsChartGroup.valueOf("NONE"));
        assertNotNull(StatisticsChartGroup.valueOf("PRIMARY"));
        assertNotNull(StatisticsChartGroup.valueOf("SECONDARY"));
    }

    @Test
    void testEnumCount() {
        assertEquals(3, StatisticsChartGroup.values().length, "There should be exactly 3 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new StatisticsChartGroup[]{
                        StatisticsChartGroup.NONE,
                        StatisticsChartGroup.PRIMARY,
                        StatisticsChartGroup.SECONDARY
                },
                StatisticsChartGroup.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> StatisticsChartGroup.valueOf("INVALID")
        );
        assertTrue(exception.getMessage().contains("INVALID"));
    }

    @Test
    void testValueOfNullThrows() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> StatisticsChartGroup.valueOf(null)
        );
        assertNotNull(exception.getMessage());
    }

}
