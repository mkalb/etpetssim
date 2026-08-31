package de.mkalb.etpetssim.simulations.core.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationStateTest {

    @Test
    void testEnumValues() {
        assertArrayEquals(new SimulationState[]{
                SimulationState.READY,
                SimulationState.INITIALIZING,
                SimulationState.RUNNING_TIMED,
                SimulationState.RUNNING_BATCH,
                SimulationState.PAUSING_BATCH,
                SimulationState.PAUSED,
                SimulationState.CANCELLING_BATCH,
                SimulationState.CANCELED,
                SimulationState.FINISHED,
                SimulationState.ERROR,
                SimulationState.SHUTTING_DOWN
        }, SimulationState.values());
    }

    @Test
    void testStatePredicates() {
        assertAll(
                () -> assertTrue(SimulationState.READY.isStartable()),
                () -> assertTrue(SimulationState.CANCELED.isStartable()),
                () -> assertTrue(SimulationState.FINISHED.isStartable()),
                () -> assertTrue(SimulationState.ERROR.isStartable()),
                () -> assertFalse(SimulationState.INITIALIZING.isStartable()),
                () -> assertFalse(SimulationState.INITIALIZING.isRunning()),
                () -> assertFalse(SimulationState.INITIALIZING.isPaused()),
                () -> assertTrue(SimulationState.RUNNING_TIMED.isRunning()),
                () -> assertTrue(SimulationState.RUNNING_BATCH.isRunning()),
                () -> assertTrue(SimulationState.PAUSED.isPaused())
        );
    }

}
