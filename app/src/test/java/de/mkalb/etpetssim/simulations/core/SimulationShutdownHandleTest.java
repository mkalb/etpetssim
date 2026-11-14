package de.mkalb.etpetssim.simulations.core;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SimulationShutdownHandleTest {

    @SuppressWarnings("EqualsWithItself")
    @Test
    void testCompletedReturnsSameInstance() {
        assertSame(SimulationShutdownHandle.completed(), SimulationShutdownHandle.completed());
    }

    @Test
    void testCompletedAwaitTerminationReturnsTrue() throws InterruptedException {
        assertTrue(SimulationShutdownHandle.completed().awaitTermination(0L, TimeUnit.NANOSECONDS));
    }

    @Test
    void testCompletedShutdownNowDoesNothing() {
        assertDoesNotThrow(() -> SimulationShutdownHandle.completed().shutdownNow());
    }

}
