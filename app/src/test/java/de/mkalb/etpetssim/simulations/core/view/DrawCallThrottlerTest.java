package de.mkalb.etpetssim.simulations.core.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DrawCallThrottlerTest {

    private static final int HISTORY_SIZE = 3;
    private static final int MAX_SKIPS = 2;
    private static final long THRESHOLD_MILLIS = 10L;

    private static DrawCallThrottler createThrottlerWithDurations(long... durationsMillis) {
        DrawCallThrottler throttler = new DrawCallThrottler(HISTORY_SIZE, MAX_SKIPS);
        for (long durationMillis : durationsMillis) {
            throttler.recordDurationMillis(durationMillis);
        }
        return throttler;
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void testConstructorRejectsNonPositiveHistorySize(int historySize) {
        assertThrows(IllegalArgumentException.class, () -> new DrawCallThrottler(historySize, MAX_SKIPS));
    }

    @Test
    void testConstructorRejectsNegativeMaxSkips() {
        assertThrows(IllegalArgumentException.class, () -> new DrawCallThrottler(HISTORY_SIZE, -1));
    }

    @Test
    void testShouldSkipReturnsFalseBeforeHistoryIsFull() {
        DrawCallThrottler throttler = createThrottlerWithDurations(100L, 100L);

        assertFalse(throttler.shouldSkip(THRESHOLD_MILLIS));
    }

    @Test
    void testShouldSkipReturnsFalseWhenAverageDoesNotExceedThreshold() {
        DrawCallThrottler throttler = createThrottlerWithDurations(10L, 10L, 10L);

        assertFalse(throttler.shouldSkip(THRESHOLD_MILLIS));
    }

    @Test
    void testShouldSkipReturnsTrueWhenAverageExceedsThreshold() {
        DrawCallThrottler throttler = createThrottlerWithDurations(100L, 100L, 100L);

        assertTrue(throttler.shouldSkip(THRESHOLD_MILLIS));
    }

    @Test
    void testShouldSkipLimitsConsecutiveSkips() {
        DrawCallThrottler throttler = createThrottlerWithDurations(100L, 100L, 100L);

        boolean firstSkip = throttler.shouldSkip(THRESHOLD_MILLIS);
        boolean secondSkip = throttler.shouldSkip(THRESHOLD_MILLIS);
        boolean thirdSkip = throttler.shouldSkip(THRESHOLD_MILLIS);
        boolean fourthSkip = throttler.shouldSkip(THRESHOLD_MILLIS);

        // After MAX_SKIPS skips in a row, one draw is forced and the counter starts again.
        assertAll(
                () -> assertTrue(firstSkip),
                () -> assertTrue(secondSkip),
                () -> assertFalse(thirdSkip),
                () -> assertTrue(fourthSkip)
        );
    }

    @Test
    void testRecordDurationMillis() {
        DrawCallThrottler throttler = createThrottlerWithDurations(10L, 21L);

        assertEquals(15L, throttler.getAverageDurationMillis());
    }

    @Test
    void testRecordDurationMillisDropsOldestDurationWhenHistoryIsFull() {
        DrawCallThrottler throttler = createThrottlerWithDurations(10L, 20L, 30L, 40L);

        assertEquals(30L, throttler.getAverageDurationMillis());
    }

    @Test
    void testGetAverageDurationMillis() {
        DrawCallThrottler throttler = createThrottlerWithDurations();

        assertEquals(0L, throttler.getAverageDurationMillis());
    }

}
