package de.mkalb.etpetssim.engine.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class WorkCheckpointsTest {

    private static boolean isCancellationCheckpoint(int zeroBasedLoopIndex, int mask) {
        return (zeroBasedLoopIndex & mask) == 0;
    }

    @SuppressWarnings("ConstantValue")
    @Test
    void testCancellationCheckIntervalIsPowerOfTwo() {
        int interval = WorkCheckpoints.CANCELLATION_CHECK_INTERVAL;

        assertAll(
                () -> assertTrue(interval > 0),
                () -> assertEquals(0, interval & (interval - 1)),
                () -> assertEquals(interval - 1, WorkCheckpoints.CANCELLATION_CHECK_MASK)
        );
    }

    @Test
    void testCancellationCheckMaskIdentifiesZeroBasedCheckpoints() {
        int interval = WorkCheckpoints.CANCELLATION_CHECK_INTERVAL;
        int mask = WorkCheckpoints.CANCELLATION_CHECK_MASK;

        assertAll(
                () -> assertTrue(isCancellationCheckpoint(0, mask)),
                () -> assertFalse(isCancellationCheckpoint(1, mask)),
                () -> assertFalse(isCancellationCheckpoint(interval - 1, mask)),
                () -> assertTrue(isCancellationCheckpoint(interval, mask)),
                () -> assertTrue(isCancellationCheckpoint(2 * interval, mask))
        );
    }

}
