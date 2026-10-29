package de.mkalb.etpetssim.simulations.core.shared;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testIsStartable() {
        for (SimulationState state : SimulationState.values()) {
            assertEquals(STARTABLE_STATES.contains(state), state.isStartable(), state.name());
        }
    }

    @Test
    void testIsRunning() {
        for (SimulationState state : SimulationState.values()) {
            assertEquals(RUNNING_STATES.contains(state), state.isRunning(), state.name());
        }
    }

    @Test
    void testIsPaused() {
        for (SimulationState state : SimulationState.values()) {
            assertEquals(PAUSED_STATES.contains(state), state.isPaused(), state.name());
        }
    }

}
