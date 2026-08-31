package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationInitializationCancellationTest {

    @Test
    void testNoneDoesNotCancel() {
        assertDoesNotThrow(SimulationInitializationCancellation.none()::checkCanceled);
    }

    @Test
    void testInterruptionAwareCancelsWhenThreadIsInterrupted() {
        Thread.currentThread().interrupt();

        try {
            assertThrows(SimulationInitializationCanceledException.class,
                    SimulationInitializationCancellation.interruptionAware()::checkCanceled);
        } finally {
            Thread.interrupted();
        }
    }

}
