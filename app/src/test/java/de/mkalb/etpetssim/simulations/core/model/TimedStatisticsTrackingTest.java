package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.forest.model.*;
import de.mkalb.etpetssim.simulations.wator.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class TimedStatisticsTrackingTest {

    private static final double DOUBLE_DELTA = 1.0e-9d;

    private static ConwayConfig createConwayConfig() {
        return new ConwayConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ConwayConstraints.GRID_WIDTH_DEFAULT,
                ConwayConstraints.GRID_HEIGHT_DEFAULT,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT
        );
    }

    private static ForestConfig createForestConfig() {
        return new ForestConfig(
                ForestConstraints.CELL_SHAPE_DEFAULT,
                ForestConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ForestConstraints.GRID_WIDTH_DEFAULT,
                ForestConstraints.GRID_HEIGHT_DEFAULT,
                ForestConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ForestConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                ForestConstraints.TREE_DENSITY_DEFAULT,
                ForestConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ForestConstraints.TREE_GROWTH_PROBABILITY_DEFAULT,
                ForestConstraints.LIGHTNING_IGNITION_PROBABILITY_DEFAULT
        );
    }

    private static WatorConfig createWatorConfig() {
        return new WatorConfig(
                WatorConstraints.CELL_SHAPE_DEFAULT,
                WatorConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                WatorConstraints.GRID_WIDTH_DEFAULT,
                WatorConstraints.GRID_HEIGHT_DEFAULT,
                WatorConstraints.CELL_EDGE_LENGTH_DEFAULT,
                WatorConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                WatorConstraints.FISH_PERCENT_DEFAULT,
                WatorConstraints.SHARK_PERCENT_DEFAULT,
                WatorConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                WatorConstraints.FISH_MAX_AGE_DEFAULT,
                WatorConstraints.FISH_MIN_REPRODUCTION_AGE_DEFAULT,
                WatorConstraints.FISH_MIN_REPRODUCTION_INTERVAL_DEFAULT,
                WatorConstraints.SHARK_MAX_AGE_DEFAULT,
                WatorConstraints.SHARK_BIRTH_ENERGY_DEFAULT,
                WatorConstraints.SHARK_ENERGY_LOSS_PER_STEP_DEFAULT,
                WatorConstraints.SHARK_ENERGY_GAIN_PER_FISH_DEFAULT,
                WatorConstraints.SHARK_MIN_REPRODUCTION_AGE_DEFAULT,
                WatorConstraints.SHARK_MIN_REPRODUCTION_ENERGY_DEFAULT,
                WatorConstraints.SHARK_MIN_REPRODUCTION_INTERVAL_DEFAULT
        );
    }

    @Test
    void testConstructorRecordsStepZeroSample() {
        ConwaySimulationManager manager = new ConwaySimulationManager(createConwayConfig());

        var history = manager.statisticsHistory();
        assertAll(
                () -> assertEquals(1, history.size()),
                () -> assertEquals(0, history.getFirst().stepCount()),
                () -> assertEquals(0, history.getFirst().stepTimingStatistics().sumNanos())
        );
    }

    @Test
    void testExecuteStepsRecordsEveryExecutedStepWithoutDuplicateFinalSample() {
        ConwaySimulationManager manager = new ConwaySimulationManager(createConwayConfig());

        int executeCount = 7;
        manager.executeSteps(executeCount, false, () -> {
        });

        var history = manager.statisticsHistory();
        long finalStepSamples = history.stream()
                                       .filter(sample -> sample.stepCount() == executeCount)
                                       .count();
        assertAll(
                () -> assertEquals(executeCount + 1, history.size()),
                () -> assertEquals(executeCount, history.getLast().stepCount()),
                () -> assertEquals(1L, finalStepSamples)
        );
    }

    @Test
    void testConwayGenericExtremaMatchesTypedMax() {
        ConwaySimulationManager manager = new ConwaySimulationManager(createConwayConfig());

        manager.executeSteps(20, false, () -> {
        });

        double genericAliveMax = manager.statisticsExtrema().maximumValues().get("aliveCells");
        assertEquals(manager.statistics().getMaxAliveCells(), genericAliveMax, DOUBLE_DELTA);
    }

    @Test
    void testForestGenericExtremaMatchesTypedMaxima() {
        ForestSimulationManager manager = new ForestSimulationManager(createForestConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericMaxima = manager.statisticsExtrema().maximumValues();
        assertAll(
                () -> assertEquals(manager.statistics().getMaxTreeCells(), genericMaxima.get("treeCells"), DOUBLE_DELTA),
                () -> assertEquals(manager.statistics().getMaxBurningCells(), genericMaxima.get("burningCells"), DOUBLE_DELTA)
        );
    }

    @Test
    void testWatorGenericExtremaMatchesTypedMinMax() {
        WatorSimulationManager manager = new WatorSimulationManager(createWatorConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertEquals(manager.statistics().getMinFishCells(), genericExtrema.minimumValues().get("fishCells"), DOUBLE_DELTA),
                () -> assertEquals(manager.statistics().getMaxFishCells(), genericExtrema.maximumValues().get("fishCells"), DOUBLE_DELTA),
                () -> assertEquals(manager.statistics().getMinSharkCells(), genericExtrema.minimumValues().get("sharkCells"), DOUBLE_DELTA),
                () -> assertEquals(manager.statistics().getMaxSharkCells(), genericExtrema.maximumValues().get("sharkCells"), DOUBLE_DELTA)
        );
    }

}

