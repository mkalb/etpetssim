package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class StatisticsExtremumTest {

    @Test
    void testRecord() {
        var extremum = new StatisticsExtremum(42.5d, 7);
        var extremumAtZero = new StatisticsExtremum(1.0d, 0);

        assertAll(
                () -> assertEquals(42.5d, extremum.value()),
                () -> assertEquals(7, extremum.stepCount()),
                () -> assertEquals(0, extremumAtZero.stepCount())
        );
    }

    @Test
    void testConstructorRejectsNonFiniteValues() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsExtremum(Double.NaN, 0)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StatisticsExtremum(Double.POSITIVE_INFINITY, 0)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StatisticsExtremum(Double.NEGATIVE_INFINITY, 0))
        );
    }

    @Test
    void testConstructorRejectsNegativeStepCount() {
        assertThrows(IllegalArgumentException.class, () -> new StatisticsExtremum(1.0d, -1));
    }

    @Test
    void testIntValue() {
        assertAll(
                () -> assertEquals(42, new StatisticsExtremum(42.9d, 0).intValue()),
                () -> assertEquals(-42, new StatisticsExtremum(-42.9d, 0).intValue()),
                () -> assertEquals(Integer.MAX_VALUE, new StatisticsExtremum(Double.MAX_VALUE, 0).intValue()),
                () -> assertEquals(Integer.MIN_VALUE, new StatisticsExtremum(-Double.MAX_VALUE, 0).intValue())
        );
    }

}
