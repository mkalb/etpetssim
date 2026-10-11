package de.mkalb.etpetssim.simulations.core;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

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
