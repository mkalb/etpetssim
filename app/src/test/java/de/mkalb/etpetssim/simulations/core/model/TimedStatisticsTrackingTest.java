package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.etpets.model.*;
import de.mkalb.etpetssim.simulations.forest.model.*;
import de.mkalb.etpetssim.simulations.langton.model.*;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules;
import de.mkalb.etpetssim.simulations.rebounding.model.*;
import de.mkalb.etpetssim.simulations.snake.model.*;
import de.mkalb.etpetssim.simulations.snake.shared.SnakeDeathMode;
import de.mkalb.etpetssim.simulations.sugar.model.*;
import de.mkalb.etpetssim.simulations.wator.model.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class TimedStatisticsTrackingTest {

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

    private static EtpetsConfig createEtpetsConfig() {
        return new EtpetsConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.GRID_WIDTH_DEFAULT,
                EtpetsConstraints.GRID_HEIGHT_DEFAULT,
                EtpetsConstraints.CELL_EDGE_LENGTH_DEFAULT,
                EtpetsConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                EtpetsConstraints.ROCK_PERCENT_DEFAULT,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT
        );
    }

    private static SnakeConfig createSnakeConfig() {
        return new SnakeConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.GRID_WIDTH_DEFAULT,
                SnakeConstraints.GRID_HEIGHT_DEFAULT,
                SnakeConstraints.CELL_EDGE_LENGTH_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                SnakeDeathMode.RESPAWN,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT
        );
    }

    private static SugarConfig createSugarConfig() {
        return new SugarConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.GRID_WIDTH_DEFAULT,
                SugarConstraints.GRID_HEIGHT_DEFAULT,
                SugarConstraints.CELL_EDGE_LENGTH_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT
        );
    }

    private static ReboundingConfig createReboundingConfig() {
        return new ReboundingConfig(
                ReboundingConstraints.CELL_SHAPE_DEFAULT,
                ReboundingConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ReboundingConstraints.GRID_WIDTH_DEFAULT,
                ReboundingConstraints.GRID_HEIGHT_DEFAULT,
                ReboundingConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ReboundingConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                ReboundingConstraints.VERTICAL_WALLS_DEFAULT,
                ReboundingConstraints.MOVING_ENTITY_PERCENT_DEFAULT,
                ReboundingConstraints.NEIGHBORHOOD_MODE_DEFAULT
        );
    }

    private static LangtonConfig createLangtonConfig() {
        return new LangtonConfig(
                LangtonConstraints.CELL_SHAPE_DEFAULT,
                LangtonConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LangtonConstraints.GRID_WIDTH_MIN,
                LangtonConstraints.GRID_HEIGHT_MIN,
                LangtonConstraints.CELL_EDGE_LENGTH_DEFAULT,
                LangtonConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                LangtonMovementRules.fromString(LangtonConstraints.RULE_DEFAULT)
        );
    }

    private static void assertStepZeroSample(AbstractTimedSimulationManager<?, ?, ?, ?> manager) {
        var history = manager.statisticsHistory();
        assertAll(
                manager.getClass().getSimpleName(),
                () -> assertEquals(1, history.size()),
                () -> assertEquals(0, history.getFirst().stepCount()),
                () -> assertEquals(0, history.getFirst().stepTimingStatistics().sumNanos())
        );
    }

    @Test
    void testAllTimedManagerConstructorsRecordStepZeroSample() {
        List<AbstractTimedSimulationManager<?, ?, ?, ?>> managers = List.of(
                new ConwaySimulationManager(createConwayConfig()),
                new EtpetsSimulationManager(createEtpetsConfig()),
                new ForestSimulationManager(createForestConfig()),
                new LangtonSimulationManager(createLangtonConfig()),
                new ReboundingSimulationManager(createReboundingConfig()),
                new SnakeSimulationManager(createSnakeConfig()),
                new SugarSimulationManager(createSugarConfig()),
                new WatorSimulationManager(createWatorConfig())
        );

        assertAll(
                managers.stream()
                        .map(manager -> () -> assertStepZeroSample(manager))
        );
    }

    @Test
    void testExecuteStepRecordsOneSample() {
        ConwaySimulationManager manager = new ConwaySimulationManager(createConwayConfig());

        manager.executeStep();

        var history = manager.statisticsHistory();
        assertAll(
                () -> assertEquals(2, history.size()),
                () -> assertEquals(1, history.getLast().stepCount())
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
    void testConwayGenericExtremaMaxAliveCellsIsFiniteAndPositive() {
        ConwaySimulationManager manager = new ConwaySimulationManager(createConwayConfig());

        manager.executeSteps(20, false, () -> {
        });

        double genericAliveMax = manager.statisticsExtrema().maximumValues().get(ConwayStatistics.KEY_ALIVE_CELLS).value();
        assertAll(
                () -> assertTrue(manager.statisticsExtrema().minimumValues().containsKey(ConwayStatistics.KEY_ALIVE_CELLS)),
                () -> assertTrue(manager.statisticsExtrema().maximumValues().containsKey(ConwayStatistics.KEY_ALIVE_CELLS)),
                () -> assertTrue(Double.isFinite(genericAliveMax)),
                () -> assertTrue(genericAliveMax >= 0.0d),
                () -> assertFalse(manager.statisticsExtrema().minimumValues().containsKey(ConwayStatistics.KEY_CHANGED_CELLS)),
                () -> assertTrue(manager.statisticsExtrema().maximumValues().containsKey(ConwayStatistics.KEY_CHANGED_CELLS)),
                () -> assertTrue(manager.statisticsExtrema().minimumValues().containsKey(ConwayStatistics.KEY_DEAD_CELLS)),
                () -> assertTrue(manager.statisticsExtrema().maximumValues().containsKey(ConwayStatistics.KEY_DEAD_CELLS))
        );
    }

    @Test
    void testForestGenericExtremaMaximaAreFiniteAndNonNegative() {
        ForestSimulationManager manager = new ForestSimulationManager(createForestConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericMaxima = manager.statisticsExtrema().maximumValues();
        var genericMinima = manager.statisticsExtrema().minimumValues();
        assertAll(
                () -> assertTrue(genericMaxima.containsKey(ForestStatistics.KEY_EMPTY_CELLS)),
                () -> assertTrue(Double.isFinite(genericMaxima.get(ForestStatistics.KEY_EMPTY_CELLS).value())),
                () -> assertTrue(genericMaxima.containsKey(ForestStatistics.KEY_TREE_CELLS)),
                () -> assertTrue(Double.isFinite(genericMaxima.get(ForestStatistics.KEY_TREE_CELLS).value())),
                () -> assertTrue(genericMaxima.get(ForestStatistics.KEY_TREE_CELLS).value() >= 0.0d),
                () -> assertTrue(genericMinima.containsKey(ForestStatistics.KEY_TREE_CELLS)),
                () -> assertTrue(Double.isFinite(genericMinima.get(ForestStatistics.KEY_TREE_CELLS).value())),
                () -> assertTrue(genericMaxima.containsKey(ForestStatistics.KEY_BURNING_CELLS)),
                () -> assertTrue(Double.isFinite(genericMaxima.get(ForestStatistics.KEY_BURNING_CELLS).value())),
                () -> assertTrue(genericMaxima.get(ForestStatistics.KEY_BURNING_CELLS).value() >= 0.0d)
        );
    }

    @Test
    void testWatorGenericExtremaMinMaxAreFinite() {
        WatorSimulationManager manager = new WatorSimulationManager(createWatorConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertTrue(genericExtrema.minimumValues().containsKey(WatorStatistics.KEY_FISH_CELLS)),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get(WatorStatistics.KEY_FISH_CELLS).value())),
                () -> assertTrue(genericExtrema.maximumValues().containsKey(WatorStatistics.KEY_FISH_CELLS)),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get(WatorStatistics.KEY_FISH_CELLS).value())),
                () -> assertTrue(genericExtrema.minimumValues().containsKey(WatorStatistics.KEY_SHARK_CELLS)),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get(WatorStatistics.KEY_SHARK_CELLS).value())),
                () -> assertTrue(genericExtrema.maximumValues().containsKey(WatorStatistics.KEY_SHARK_CELLS)),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get(WatorStatistics.KEY_SHARK_CELLS).value()))
        );
    }

    @Test
    void testEtpetsGenericExtremaPresenceAndNoneAbsence() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createEtpetsConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertTrue(genericExtrema.minimumValues().containsKey(EtpetsStatistics.KEY_ACTIVE_PET_CELLS)),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get(EtpetsStatistics.KEY_ACTIVE_PET_CELLS).value())),
                () -> assertTrue(genericExtrema.maximumValues().containsKey(EtpetsStatistics.KEY_ACTIVE_PET_CELLS)),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get(EtpetsStatistics.KEY_ACTIVE_PET_CELLS).value())),
                () -> assertFalse(genericExtrema.minimumValues().containsKey(EtpetsStatistics.KEY_EGG_CELLS)),
                () -> assertTrue(genericExtrema.maximumValues().containsKey(EtpetsStatistics.KEY_EGG_CELLS)),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get(EtpetsStatistics.KEY_EGG_CELLS).value())),
                () -> assertFalse(genericExtrema.minimumValues().containsKey(EtpetsStatistics.KEY_CUMULATIVE_PET_DEATH_COUNT)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(EtpetsStatistics.KEY_CUMULATIVE_PET_DEATH_COUNT))
        );
    }

    @Test
    void testSnakeGenericExtremaPresenceAndNoneAbsence() {
        SnakeSimulationManager manager = new SnakeSimulationManager(createSnakeConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertFalse(genericExtrema.minimumValues().containsKey(SnakeStatistics.KEY_SNAKE_HEAD_CELLS)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(SnakeStatistics.KEY_SNAKE_HEAD_CELLS)),
                () -> assertFalse(genericExtrema.minimumValues().containsKey(SnakeStatistics.KEY_LIVING_SNAKE_HEAD_CELLS)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(SnakeStatistics.KEY_LIVING_SNAKE_HEAD_CELLS)),
                () -> assertFalse(genericExtrema.minimumValues().containsKey(SnakeStatistics.KEY_WALL_CELLS)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(SnakeStatistics.KEY_WALL_CELLS)),
                () -> assertFalse(genericExtrema.minimumValues().containsKey(SnakeStatistics.KEY_FOOD_CELLS)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(SnakeStatistics.KEY_FOOD_CELLS)),
                () -> assertFalse(genericExtrema.minimumValues().containsKey(SnakeStatistics.KEY_CUMULATIVE_SNAKE_DEATH_COUNT)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(SnakeStatistics.KEY_CUMULATIVE_SNAKE_DEATH_COUNT))
        );
    }

    @Test
    void testSugarGenericExtremaPresence() {
        SugarSimulationManager manager = new SugarSimulationManager(createSugarConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertFalse(genericExtrema.minimumValues().containsKey(SugarStatistics.KEY_RESOURCE_CELLS)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(SugarStatistics.KEY_RESOURCE_CELLS)),
                () -> assertFalse(genericExtrema.minimumValues().containsKey(SugarStatistics.KEY_AGENT_CELLS)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(SugarStatistics.KEY_AGENT_CELLS))
        );
    }

    @Test
    void testReboundingGenericExtremaPresenceAndStaticWalls() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createReboundingConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertFalse(genericExtrema.minimumValues().containsKey(ReboundingStatistics.KEY_MOVING_ENTITY_CELLS)),
                () -> assertTrue(genericExtrema.maximumValues().containsKey(ReboundingStatistics.KEY_MOVING_ENTITY_CELLS)),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get(ReboundingStatistics.KEY_MOVING_ENTITY_CELLS).value())),
                () -> assertFalse(genericExtrema.minimumValues().containsKey(ReboundingStatistics.KEY_WALL_CELLS)),
                () -> assertTrue(genericExtrema.maximumValues().containsKey(ReboundingStatistics.KEY_WALL_CELLS))
        );
    }

    @Test
    void testLangtonGenericMaxPresenceAndNoneAbsence() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createLangtonConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertFalse(genericExtrema.minimumValues().containsKey(LangtonStatistics.KEY_VISITED_CELLS)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(LangtonStatistics.KEY_VISITED_CELLS)),
                () -> assertFalse(genericExtrema.minimumValues().containsKey(LangtonStatistics.KEY_ANT_CELLS)),
                () -> assertFalse(genericExtrema.maximumValues().containsKey(LangtonStatistics.KEY_ANT_CELLS))
        );
    }

    // --- Golden-value regression tests ---
    // Expected values captured with seed=1, 20 steps, default constraints.
    // Both the batch path (executeSteps(20)) and the single-step path
    // (executeSteps(1) x20) must produce identical extrema.

    @Test
    void testConwayGoldenExtremaExactValues() {
        ConwaySimulationManager batch = new ConwaySimulationManager(createConwayConfig());
        batch.executeSteps(20, false, () -> {
        });

        ConwaySimulationManager singleStep = new ConwaySimulationManager(createConwayConfig());
        for (int i = 0; i < 20; i++) {
            singleStep.executeSteps(1, false, () -> {
            });
        }

        assertAll(
                () -> assertEquals(3575, batch.statisticsExtrema().minimumValues().get(ConwayStatistics.KEY_ALIVE_CELLS).value(),
                        "batch: minAliveCells"),
                () -> assertEquals(6945, batch.statisticsExtrema().maximumValues().get(ConwayStatistics.KEY_ALIVE_CELLS).value(),
                        "batch: maxAliveCells"),
                () -> assertEquals(6475, batch.statisticsExtrema().maximumValues().get(ConwayStatistics.KEY_CHANGED_CELLS).value(),
                        "batch: maxChangedCells"),
                () -> assertEquals(3575, singleStep.statisticsExtrema().minimumValues().get(ConwayStatistics.KEY_ALIVE_CELLS).value(),
                        "singleStep: minAliveCells"),
                () -> assertEquals(6945, singleStep.statisticsExtrema().maximumValues().get(ConwayStatistics.KEY_ALIVE_CELLS).value(),
                        "singleStep: maxAliveCells"),
                () -> assertEquals(6475, singleStep.statisticsExtrema().maximumValues().get(ConwayStatistics.KEY_CHANGED_CELLS).value(),
                        "singleStep: maxChangedCells")
        );
    }

    @Test
    void testForestGoldenExtremaExactValues() {
        ForestSimulationManager batch = new ForestSimulationManager(createForestConfig());
        batch.executeSteps(20, false, () -> {
        });

        ForestSimulationManager singleStep = new ForestSimulationManager(createForestConfig());
        for (int i = 0; i < 20; i++) {
            singleStep.executeSteps(1, false, () -> {
            });
        }

        assertAll(
                () -> assertEquals(4000, batch.statisticsExtrema().maximumValues().get(ForestStatistics.KEY_EMPTY_CELLS).value(),
                        "batch: maxEmptyCells"),
                () -> assertEquals(1000, batch.statisticsExtrema().minimumValues().get(ForestStatistics.KEY_TREE_CELLS).value(),
                        "batch: minTreeCells"),
                () -> assertEquals(1037, batch.statisticsExtrema().maximumValues().get(ForestStatistics.KEY_TREE_CELLS).value(),
                        "batch: maxTreeCells"),
                () -> assertEquals(17, batch.statisticsExtrema().maximumValues().get(ForestStatistics.KEY_BURNING_CELLS).value(),
                        "batch: maxBurningCells"),
                () -> assertEquals(4000, singleStep.statisticsExtrema().maximumValues().get(ForestStatistics.KEY_EMPTY_CELLS).value(),
                        "singleStep: maxEmptyCells"),
                () -> assertEquals(1000, singleStep.statisticsExtrema().minimumValues().get(ForestStatistics.KEY_TREE_CELLS).value(),
                        "singleStep: minTreeCells"),
                () -> assertEquals(1037, singleStep.statisticsExtrema().maximumValues().get(ForestStatistics.KEY_TREE_CELLS).value(),
                        "singleStep: maxTreeCells"),
                () -> assertEquals(17, singleStep.statisticsExtrema().maximumValues().get(ForestStatistics.KEY_BURNING_CELLS).value(),
                        "singleStep: maxBurningCells")
        );
    }

    @Test
    void testWatorGoldenExtremaExactValues() {
        WatorSimulationManager batch = new WatorSimulationManager(createWatorConfig());
        batch.executeSteps(20, false, () -> {
        });

        WatorSimulationManager singleStep = new WatorSimulationManager(createWatorConfig());
        for (int i = 0; i < 20; i++) {
            singleStep.executeSteps(1, false, () -> {
            });
        }

        assertAll(
                () -> assertEquals(1900, batch.statisticsExtrema().minimumValues().get(WatorStatistics.KEY_FISH_CELLS).value(),
                        "batch: minFishCells"),
                () -> assertEquals(6127, batch.statisticsExtrema().maximumValues().get(WatorStatistics.KEY_FISH_CELLS).value(),
                        "batch: maxFishCells"),
                () -> assertEquals(1000, batch.statisticsExtrema().minimumValues().get(WatorStatistics.KEY_SHARK_CELLS).value(),
                        "batch: minSharkCells"),
                () -> assertEquals(3002, batch.statisticsExtrema().maximumValues().get(WatorStatistics.KEY_SHARK_CELLS).value(),
                        "batch: maxSharkCells"),
                () -> assertEquals(1900, singleStep.statisticsExtrema().minimumValues().get(WatorStatistics.KEY_FISH_CELLS).value(),
                        "singleStep: minFishCells"),
                () -> assertEquals(6127, singleStep.statisticsExtrema().maximumValues().get(WatorStatistics.KEY_FISH_CELLS).value(),
                        "singleStep: maxFishCells"),
                () -> assertEquals(1000, singleStep.statisticsExtrema().minimumValues().get(WatorStatistics.KEY_SHARK_CELLS).value(),
                        "singleStep: minSharkCells"),
                () -> assertEquals(3002, singleStep.statisticsExtrema().maximumValues().get(WatorStatistics.KEY_SHARK_CELLS).value(),
                        "singleStep: maxSharkCells")
        );
    }

}

