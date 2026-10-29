package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class SimulationInitializationCanceledExceptionTest {

    @Test
    void testConstructor() {
        var exception = new SimulationInitializationCanceledException();

        assertAll(
                () -> assertEquals("Simulation initialization was canceled.", exception.getMessage()),
                () -> assertNull(exception.getCause()),
                () -> assertArrayEquals(new Throwable[0], exception.getSuppressed()),
                () -> assertArrayEquals(new StackTraceElement[0], exception.getStackTrace())
        );
    }

}
