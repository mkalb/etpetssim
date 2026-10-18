package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticsExtremaModeTest {

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
