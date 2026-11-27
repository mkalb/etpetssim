package de.mkalb.etpetssim.simulations.conway.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

final class ConwaySimulationManagerTest {

    private static final int STEPS = 50;

    private static ConwaySimulationManager createManagerAfterSteps(long seed) {
        var manager = new ConwaySimulationManager(new ConwayConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ConwayConstraints.GRID_WIDTH_DEFAULT,
                ConwayConstraints.GRID_HEIGHT_DEFAULT,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                seed,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT
        ));
        manager.executeSteps(STEPS, false, () -> {
        });
        return manager;
    }

    @Test
    void testExecuteStepsIsReproducibleWithSameSeed() {
        ConwaySimulationManager first = createManagerAfterSteps(1L);
        ConwaySimulationManager second = createManagerAfterSteps(1L);
        ConwaySimulationManager otherSeed = createManagerAfterSteps(2L);

        assertAll(
                () -> assertEquals(first.currentGridModel().allCells(), second.currentGridModel().allCells()),
                () -> assertEquals(first.statistics().aliveCells(), second.statistics().aliveCells()),
                () -> assertEquals(first.statistics().changedCells(), second.statistics().changedCells()),
                () -> assertEquals(STEPS, second.statistics().stepCount()),
                // A different seed must change the run; otherwise the equality above would prove nothing.
                () -> assertNotEquals(first.currentGridModel().allCells(), otherSeed.currentGridModel().allCells())
        );
    }

}
