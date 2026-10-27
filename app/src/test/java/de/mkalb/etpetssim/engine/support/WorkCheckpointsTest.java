package de.mkalb.etpetssim.engine.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class WorkCheckpointsTest {

    @SuppressWarnings("ConstantValue")
    @Test
    void testCancellationCheckInterval() {
        int interval = WorkCheckpoints.CANCELLATION_CHECK_INTERVAL;

        assertAll(
                () -> assertTrue(interval > 0),
                () -> assertEquals(0, interval & (interval - 1))
        );
    }

    @Test
    void testIsCancellationCheckpoint() {
        int interval = WorkCheckpoints.CANCELLATION_CHECK_INTERVAL;

        assertAll(
                () -> assertTrue(WorkCheckpoints.isCancellationCheckpoint(0)),
                () -> assertFalse(WorkCheckpoints.isCancellationCheckpoint(1)),
                () -> assertFalse(WorkCheckpoints.isCancellationCheckpoint(interval - 1)),
                () -> assertTrue(WorkCheckpoints.isCancellationCheckpoint(interval)),
                () -> assertFalse(WorkCheckpoints.isCancellationCheckpoint(interval + 1)),
                () -> assertTrue(WorkCheckpoints.isCancellationCheckpoint(2 * interval))
        );
    }

}
