package de.mkalb.etpetssim.simulations.forest.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

final class ForestSimulationManagerTest {

    private static final int STEPS = 50;

    private static ForestSimulationManager createManagerAfterSteps(long seed) {
        var manager = new ForestSimulationManager(new ForestConfig(
                ForestConstraints.CELL_SHAPE_DEFAULT,
                ForestConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ForestConstraints.GRID_WIDTH_DEFAULT,
                ForestConstraints.GRID_HEIGHT_DEFAULT,
                ForestConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ForestConstraints.CELL_DISPLAY_MODE_DEFAULT,
                seed,
                0.5d,
                ForestConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                0.05d,
                0.001d
        ));
        manager.executeSteps(STEPS, false, () -> {
        });
        return manager;
    }

    @Test
    void testExecuteStepsIsReproducibleWithSameSeed() {
        ForestSimulationManager first = createManagerAfterSteps(1L);
        ForestSimulationManager second = createManagerAfterSteps(1L);
        ForestSimulationManager otherSeed = createManagerAfterSteps(2L);

        assertAll(
                () -> assertEquals(first.currentGridModel().allCells(), second.currentGridModel().allCells()),
                () -> assertEquals(first.statistics().treeCells(), second.statistics().treeCells()),
                () -> assertEquals(first.statistics().burningCells(), second.statistics().burningCells()),
                () -> assertEquals(first.statistics().cumulativeLightningStrikeCount(),
                        second.statistics().cumulativeLightningStrikeCount()),
                () -> assertEquals(first.statistics().cumulativeBurnedTreeCount(),
                        second.statistics().cumulativeBurnedTreeCount()),
                () -> assertEquals(STEPS, second.statistics().stepCount()),
                // A different seed must change the run; otherwise the equality above would prove nothing.
                () -> assertNotEquals(first.currentGridModel().allCells(), otherSeed.currentGridModel().allCells())
        );
    }

}
