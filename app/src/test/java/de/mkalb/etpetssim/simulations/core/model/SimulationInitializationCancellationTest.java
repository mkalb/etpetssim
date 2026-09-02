package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationInitializationCancellationTest {

    @Test
    void testCanceledExceptionContract() {
        var exception = new SimulationInitializationCanceledException();

        assertAll(
                () -> assertEquals("Simulation initialization was canceled.", exception.getMessage()),
                () -> assertNull(exception.getCause()),
                () -> assertArrayEquals(new Throwable[0], exception.getSuppressed()),
                () -> assertArrayEquals(new StackTraceElement[0], exception.getStackTrace())
        );
    }

    @Test
    void testNoneDoesNotCancel() {
        assertDoesNotThrow(SimulationInitializationCancellation.none()::checkCanceled);
    }

    @Test
    void testInterruptionAwareDoesNotCancelWhenThreadIsNotInterrupted() {
        assertFalse(Thread.currentThread().isInterrupted());
        assertDoesNotThrow(SimulationInitializationCancellation.interruptionAware()::checkCanceled);
    }

    @Test
    void testInterruptionAwareCancelsWhenThreadIsInterrupted() {
        Thread.currentThread().interrupt();

        try {
            assertThrows(SimulationInitializationCanceledException.class,
                    SimulationInitializationCancellation.interruptionAware()::checkCanceled);
        } finally {
            assertTrue(Thread.interrupted());
        }
    }

}
