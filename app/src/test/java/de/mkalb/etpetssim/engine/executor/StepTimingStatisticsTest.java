package de.mkalb.etpetssim.engine.executor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class StepTimingStatisticsTest {

    private static final long ZERO = 0L;
    private static final long ONE = 1L;
    private static final long NINE = 9L;
    private static final long TEN = 10L;
    private static final long ELEVEN = 11L;
    private static final long TWELVE = 12L;
    private static final long FIFTEEN = 15L;
    private static final long TWENTY = 20L;
    private static final long TWENTY_ONE = 21L;
    private static final long FIFTY = 50L;
    private static final long HUNDRED_TWENTY = 120L;

    @Test
    void testEmptyFactoryReturnsAllZeros() {
        StepTimingStatistics statistics = StepTimingStatistics.empty();

        assertAll(
                () -> assertEquals(ZERO, statistics.currentNanos()),
                () -> assertEquals(ZERO, statistics.minNanos()),
                () -> assertEquals(ZERO, statistics.maxNanos()),
                () -> assertEquals(ZERO, statistics.sumNanos()),
                () -> assertEquals(ZERO, statistics.avgNanos())
        );
    }

    @Test
    void testConstructorAcceptsConsistentNonEmptyValues() {
        StepTimingStatistics statistics = new StepTimingStatistics(TWELVE, TEN, TWENTY, HUNDRED_TWENTY, FIFTEEN);

        assertAll(
                () -> assertEquals(TWELVE, statistics.currentNanos()),
                () -> assertEquals(TEN, statistics.minNanos()),
                () -> assertEquals(TWENTY, statistics.maxNanos()),
                () -> assertEquals(HUNDRED_TWENTY, statistics.sumNanos()),
                () -> assertEquals(FIFTEEN, statistics.avgNanos())
        );
    }

    @Test
    void testConstructorRejectsNegativeValues() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(-ONE, ZERO, ZERO, ZERO, ZERO)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(ZERO, -ONE, ZERO, ZERO, ZERO)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(ZERO, ZERO, -ONE, ZERO, ZERO)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(ZERO, ZERO, ZERO, -ONE, ZERO)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(ZERO, ZERO, ZERO, ZERO, -ONE))
        );
    }

    @Test
    void testConstructorRejectsInconsistentRanges() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(TEN, ELEVEN, TEN, FIFTY, TEN)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(NINE, TEN, TWENTY, FIFTY, FIFTEEN)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(TWENTY_ONE, TEN, TWENTY, FIFTY, FIFTEEN)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(TWELVE, TEN, TWENTY, FIFTY, NINE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(TWELVE, TEN, TWENTY, FIFTY, TWENTY_ONE))
        );
    }

    @Test
    void testConstructorRejectsNonZeroValuesWhenSumIsZero() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(ONE, ZERO, ZERO, ZERO, ZERO)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(ZERO, ONE, ONE, ZERO, ONE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(ZERO, ZERO, ONE, ZERO, ZERO)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StepTimingStatistics(ZERO, ZERO, ZERO, ZERO, ONE))
        );
    }

}

