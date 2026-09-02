package de.mkalb.etpetssim.simulations.core;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationTerminationTest {

    @SuppressWarnings("EqualsWithItself")
    @Test
    void testCompletedReturnsSameInstance() {
        assertSame(SimulationTermination.completed(), SimulationTermination.completed());
    }

    @Test
    void testCompletedAwaitTerminationReturnsTrue() throws InterruptedException {
        assertTrue(SimulationTermination.completed().awaitTermination(0L, TimeUnit.NANOSECONDS));
    }

    @Test
    void testCompletedShutdownNowDoesNothing() {
        assertDoesNotThrow(() -> SimulationTermination.completed().shutdownNow());
    }

}
