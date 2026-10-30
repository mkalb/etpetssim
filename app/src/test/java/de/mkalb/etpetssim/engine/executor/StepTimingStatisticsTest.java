package de.mkalb.etpetssim.engine.executor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("MagicNumber")
final class StepTimingStatisticsTest {

    @Test
    void testRecord() {
        StepTimingStatistics statistics = new StepTimingStatistics(12L, 10L, 20L, 120L, 15L);
        StepTimingStatistics boundaries = new StepTimingStatistics(10L, 10L, 20L, 40L, 20L);

        assertAll(
                () -> assertEquals(12L, statistics.currentNanos()),
                () -> assertEquals(10L, statistics.minNanos()),
                () -> assertEquals(20L, statistics.maxNanos()),
                () -> assertEquals(120L, statistics.sumNanos()),
                () -> assertEquals(15L, statistics.avgNanos()),
                () -> assertEquals(10L, boundaries.currentNanos()),
                () -> assertEquals(20L, boundaries.avgNanos())
        );
    }

    @Test
    void testConstructorRejectsNegativeValues() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new StepTimingStatistics(-1L, 0L, 0L, 0L, 0L)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StepTimingStatistics(0L, -1L, 0L, 0L, 0L)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StepTimingStatistics(0L, 0L, -1L, 0L, 0L)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StepTimingStatistics(0L, 0L, 0L, -1L, 0L)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StepTimingStatistics(0L, 0L, 0L, 0L, -1L))
        );
    }

    @Test
    void testConstructorRejectsInconsistentRanges() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(10L, 11L, 10L, 50L, 10L)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(9L, 10L, 20L, 50L, 15L)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(21L, 10L, 20L, 50L, 15L)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(12L, 10L, 20L, 50L, 9L)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(12L, 10L, 20L, 50L, 21L))
        );
    }

    @Test
    void testConstructorRejectsNonZeroValuesWhenSumIsZero() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new StepTimingStatistics(1L, 0L, 0L, 0L, 0L)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StepTimingStatistics(0L, 1L, 1L, 0L, 1L)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StepTimingStatistics(0L, 0L, 1L, 0L, 0L)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StepTimingStatistics(0L, 0L, 0L, 0L, 1L))
        );
    }

    @Test
    void testEmpty() {
        assertEquals(new StepTimingStatistics(0L, 0L, 0L, 0L, 0L), StepTimingStatistics.empty());
    }

}
