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
    void testConwayGenericExtremaMaxAliveCellsIsFiniteAndPositive() {
        ConwaySimulationManager manager = new ConwaySimulationManager(createConwayConfig());

        manager.executeSteps(20, false, () -> {
        });

        double genericAliveMax = manager.statisticsExtrema().maximumValues().get("aliveCells");
        assertAll(
                () -> assertTrue(manager.statisticsExtrema().maximumValues().containsKey("aliveCells")),
                () -> assertTrue(Double.isFinite(genericAliveMax)),
                () -> assertTrue(genericAliveMax >= 0.0d)
        );
    }

    @Test
    void testForestGenericExtremaMaximaAreFiniteAndNonNegative() {
        ForestSimulationManager manager = new ForestSimulationManager(createForestConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericMaxima = manager.statisticsExtrema().maximumValues();
        assertAll(
                () -> assertTrue(genericMaxima.containsKey("treeCells")),
                () -> assertTrue(Double.isFinite(genericMaxima.get("treeCells"))),
                () -> assertTrue(genericMaxima.get("treeCells") >= 0.0d),
                () -> assertTrue(genericMaxima.containsKey("burningCells")),
                () -> assertTrue(Double.isFinite(genericMaxima.get("burningCells"))),
                () -> assertTrue(genericMaxima.get("burningCells") >= 0.0d)
        );
    }

    @Test
    void testWatorGenericExtremaMinMaxAreFinite() {
        WatorSimulationManager manager = new WatorSimulationManager(createWatorConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertTrue(genericExtrema.minimumValues().containsKey("fishCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("fishCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("fishCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("fishCells"))),
                () -> assertTrue(genericExtrema.minimumValues().containsKey("sharkCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("sharkCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("sharkCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("sharkCells")))
        );
    }

    @Test
    void testEtpetsGenericExtremaPresenceAndNoneAbsence() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createEtpetsConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertTrue(genericExtrema.minimumValues().containsKey("activePetCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("activePetCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("activePetCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("activePetCells"))),
                () -> assertTrue(genericExtrema.minimumValues().containsKey("eggCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("eggCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("eggCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("eggCells"))),
                () -> assertFalse(genericExtrema.minimumValues().containsKey("cumulativePetDeathCount")),
                () -> assertFalse(genericExtrema.maximumValues().containsKey("cumulativePetDeathCount"))
        );
    }

    @Test
    void testSnakeGenericExtremaPresenceAndNoneAbsence() {
        SnakeSimulationManager manager = new SnakeSimulationManager(createSnakeConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertTrue(genericExtrema.minimumValues().containsKey("snakeHeadCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("snakeHeadCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("snakeHeadCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("snakeHeadCells"))),
                () -> assertTrue(genericExtrema.minimumValues().containsKey("livingSnakeHeadCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("livingSnakeHeadCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("livingSnakeHeadCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("livingSnakeHeadCells"))),
                () -> assertTrue(genericExtrema.minimumValues().containsKey("wallCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("wallCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("wallCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("wallCells"))),
                () -> assertTrue(genericExtrema.minimumValues().containsKey("foodCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("foodCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("foodCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("foodCells"))),
                () -> assertFalse(genericExtrema.minimumValues().containsKey("cumulativeSnakeDeathCount")),
                () -> assertFalse(genericExtrema.maximumValues().containsKey("cumulativeSnakeDeathCount"))
        );
    }

    @Test
    void testSugarGenericExtremaPresence() {
        SugarSimulationManager manager = new SugarSimulationManager(createSugarConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertTrue(genericExtrema.minimumValues().containsKey("resourceCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("resourceCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("resourceCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("resourceCells"))),
                () -> assertTrue(genericExtrema.minimumValues().containsKey("agentCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("agentCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("agentCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("agentCells")))
        );
    }

    @Test
    void testReboundingGenericExtremaPresenceAndStaticWalls() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createReboundingConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertTrue(genericExtrema.minimumValues().containsKey("movingEntityCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("movingEntityCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("movingEntityCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("movingEntityCells"))),
                () -> assertTrue(genericExtrema.minimumValues().containsKey("wallCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.minimumValues().get("wallCells"))),
                () -> assertTrue(genericExtrema.maximumValues().containsKey("wallCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("wallCells")))
        );
    }

    @Test
    void testLangtonGenericMaxPresenceAndNoneAbsence() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createLangtonConfig());

        manager.executeSteps(20, false, () -> {
        });

        var genericExtrema = manager.statisticsExtrema();
        assertAll(
                () -> assertTrue(genericExtrema.maximumValues().containsKey("visitedCells")),
                () -> assertTrue(Double.isFinite(genericExtrema.maximumValues().get("visitedCells"))),
                () -> assertFalse(genericExtrema.minimumValues().containsKey("visitedCells")),
                () -> assertFalse(genericExtrema.minimumValues().containsKey("antCells")),
                () -> assertFalse(genericExtrema.maximumValues().containsKey("antCells"))
        );
    }

}

