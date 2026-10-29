package de.mkalb.etpetssim.simulations.core.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SimulationStepEventTest {

    @Test
    void testRecord() {
        var event = new SimulationStepEvent(true, 7, false);
        var eventAtZero = new SimulationStepEvent(false, 0, true);

        assertAll(
                () -> assertTrue(event.batchModeRunning()),
                () -> assertEquals(7, event.stepCount()),
                () -> assertFalse(event.finalStep()),
                () -> assertEquals(0, eventAtZero.stepCount())
        );
    }

    @Test
    void testConstructorRejectsNegativeStepCount() {
        assertThrows(IllegalArgumentException.class, () -> new SimulationStepEvent(false, -1, false));
    }

}
