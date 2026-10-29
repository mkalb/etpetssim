package de.mkalb.etpetssim.simulations.core;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SimulationTerminationHandleTest {

    @SuppressWarnings("EqualsWithItself")
    @Test
    void testCompletedReturnsSameInstance() {
        assertSame(SimulationTerminationHandle.completed(), SimulationTerminationHandle.completed());
    }

    @Test
    void testCompletedAwaitTerminationReturnsTrue() throws InterruptedException {
        assertTrue(SimulationTerminationHandle.completed().awaitTermination(0L, TimeUnit.NANOSECONDS));
    }

    @Test
    void testCompletedShutdownNowDoesNothing() {
        assertDoesNotThrow(() -> SimulationTerminationHandle.completed().shutdownNow());
    }

}
