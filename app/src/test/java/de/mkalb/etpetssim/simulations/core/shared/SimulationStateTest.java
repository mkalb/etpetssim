package de.mkalb.etpetssim.simulations.core.shared;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class SimulationStateTest {

    private static final Set<SimulationState> STARTABLE_STATES = Set.of(
            SimulationState.READY,
            SimulationState.CANCELED,
            SimulationState.FINISHED,
            SimulationState.ERROR);

    private static final Set<SimulationState> RUNNING_STATES = Set.of(
            SimulationState.RUNNING_TIMED,
            SimulationState.RUNNING_BATCH);

    private static final Set<SimulationState> PAUSED_STATES = Set.of(SimulationState.PAUSED);

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
    void testEnumCount() {
        assertEquals(11, SimulationState.values().length);
    }

    @Test
    void testDeclarationOrder() {
        assertAll(
                () -> assertEquals(0, SimulationState.READY.ordinal()),
                () -> assertEquals(1, SimulationState.INITIALIZING.ordinal()),
                () -> assertEquals(2, SimulationState.RUNNING_TIMED.ordinal()),
                () -> assertEquals(3, SimulationState.RUNNING_BATCH.ordinal()),
                () -> assertEquals(4, SimulationState.PAUSING_BATCH.ordinal()),
                () -> assertEquals(5, SimulationState.PAUSED.ordinal()),
                () -> assertEquals(6, SimulationState.CANCELLING_BATCH.ordinal()),
                () -> assertEquals(7, SimulationState.CANCELED.ordinal()),
                () -> assertEquals(8, SimulationState.FINISHED.ordinal()),
                () -> assertEquals(9, SimulationState.ERROR.ordinal()),
                () -> assertEquals(10, SimulationState.SHUTTING_DOWN.ordinal())
        );
    }

    @Test
    void testValueOfRejectsInvalidOrNull() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> SimulationState.valueOf("UNKNOWN")),
                () -> assertThrows(NullPointerException.class, () -> SimulationState.valueOf(null))
        );
    }

    @Test
    void testStatePredicates() {
        for (SimulationState state : SimulationState.values()) {
            assertAll(
                    state.name(),
                    () -> assertEquals(STARTABLE_STATES.contains(state), state.isStartable()),
                    () -> assertEquals(RUNNING_STATES.contains(state), state.isRunning()),
                    () -> assertEquals(PAUSED_STATES.contains(state), state.isPaused())
            );
        }
    }

}
