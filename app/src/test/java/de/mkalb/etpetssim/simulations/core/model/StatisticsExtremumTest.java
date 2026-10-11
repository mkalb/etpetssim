package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class StatisticsExtremumTest {

    @Test
    void testRecordComponents() {
        var extremum = new StatisticsExtremum(42.5d, 7L);

        assertAll(
                () -> assertEquals(42.5d, extremum.value()),
                () -> assertEquals(7L, extremum.stepCount())
        );
    }

    @Test
    void testConstructorAcceptsZeroStepCount() {
        var extremum = new StatisticsExtremum(1.0d, 0L);

        assertEquals(0L, extremum.stepCount());
    }

    @Test
    void testConstructorRejectsNonFiniteValues() {
        assertAll(
                () -> assertNotNull(assertThrows(
                        IllegalArgumentException.class,
                        () -> new StatisticsExtremum(Double.NaN, 0L)
                )),
                () -> assertNotNull(assertThrows(
                        IllegalArgumentException.class,
                        () -> new StatisticsExtremum(Double.POSITIVE_INFINITY, 0L)
                )),
                () -> assertNotNull(assertThrows(
                        IllegalArgumentException.class,
                        () -> new StatisticsExtremum(Double.NEGATIVE_INFINITY, 0L)
                ))
        );
    }

    @Test
    void testConstructorRejectsNegativeStepCount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new StatisticsExtremum(1.0d, -1L)
        );

        assertTrue(exception.getMessage().contains("stepCount"));
    }

    @Test
    void testIntValueUsesNarrowingConversion() {
        assertAll(
                () -> assertEquals(42, new StatisticsExtremum(42.9d, 0L).intValue()),
                () -> assertEquals(-42, new StatisticsExtremum(-42.9d, 0L).intValue()),
                () -> assertEquals(Integer.MAX_VALUE, new StatisticsExtremum(Double.MAX_VALUE, 0L).intValue()),
                () -> assertEquals(Integer.MIN_VALUE, new StatisticsExtremum(-Double.MAX_VALUE, 0L).intValue())
        );
    }

}
