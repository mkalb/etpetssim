package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.forest.model.*;
import de.mkalb.etpetssim.simulations.wator.model.*;

import java.util.*;

/**
 * Manual utility to re-capture extrema golden values for Conway, Forest, and Wator
 * using seed 1L and 20 steps. Run once when simulation logic changes to refresh the
 * hard-coded expectations in TimedStatisticsTrackingTest (testConway/Forest/WatorGoldenExtremaExactValues).
 *
 * <p>Current golden values (seed=1, 20 steps, default constraints):
 * <pre>
 *   Conway  - minAliveCells   : 3806 | maxAliveCells   : 6749
 *             maxChangedCells : 6353
 *   Forest  - maxEmptyCells   : 4000
 *             minTreeCells    : 1000 | maxTreeCells    : 1069 | maxBurningCells : 7
 *   Wator   - minFishCells    : 1900 | maxFishCells    : 6127
 *             minSharkCells   : 1000 | maxSharkCells   : 3002
 * </pre>
 */
@SuppressWarnings("HardcodedLineSeparator")
public final class ExtremaGoldenValueAnalyzer {

    private static final long SEED = 1L;
    private static final int STEP_COUNT = 20;
    private static final int INITIAL_BUFFER_CAPACITY = 512;

    private ExtremaGoldenValueAnalyzer() {
    }

    static void main() {
        Locale.setDefault(Locale.ROOT);
        System.out.printf(Locale.ROOT, "%s", captureValues());
    }

    private static String captureValues() {
        var sb = new StringBuilder(INITIAL_BUFFER_CAPACITY);
        appendConway(sb);
        appendForest(sb);
        appendWator(sb);
        return sb.toString();
    }

    private static ConwayConfig createConwayConfig() {
        return new ConwayConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ConwayConstraints.GRID_WIDTH_DEFAULT,
                ConwayConstraints.GRID_HEIGHT_DEFAULT,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SEED,
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
                SEED,
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
                SEED,
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

    private static int minimumValue(StatisticExtrema extrema, String key) {
        return extrema.minimumValues().get(key).intValue();
    }

    private static int maximumValue(StatisticExtrema extrema, String key) {
        return extrema.maximumValues().get(key).intValue();
    }

    private static void appendConway(StringBuilder sb) {
        SimulationManager<?, ?, ?, ?> manager = new ConwaySimulationManager(createConwayConfig());
        manager.executeSteps(STEP_COUNT, false, () -> {
        });
        var extrema = manager.statisticsExtrema();
        sb.append("=== Conway (seed=").append(SEED).append(", steps=").append(STEP_COUNT).append(") ===\n");
        sb.append("  minAliveCells    = ").append(minimumValue(extrema, ConwayStatistics.KEY_ALIVE_CELLS)).append('\n');
        sb.append("  maxAliveCells    = ").append(maximumValue(extrema, ConwayStatistics.KEY_ALIVE_CELLS)).append('\n');
        sb.append("  maxChangedCells  = ").append(maximumValue(extrema, ConwayStatistics.KEY_CHANGED_CELLS)).append('\n');
    }

    private static void appendForest(StringBuilder sb) {
        SimulationManager<?, ?, ?, ?> manager = new ForestSimulationManager(createForestConfig());
        manager.executeSteps(STEP_COUNT, false, () -> {
        });
        var extrema = manager.statisticsExtrema();
        sb.append("=== Forest (seed=").append(SEED).append(", steps=").append(STEP_COUNT).append(") ===\n");
        sb.append("  maxEmptyCells   = ").append(maximumValue(extrema, ForestStatistics.KEY_EMPTY_CELLS)).append('\n');
        sb.append("  minTreeCells    = ").append(minimumValue(extrema, ForestStatistics.KEY_TREE_CELLS)).append('\n');
        sb.append("  maxTreeCells    = ").append(maximumValue(extrema, ForestStatistics.KEY_TREE_CELLS)).append('\n');
        sb.append("  maxBurningCells = ").append(maximumValue(extrema, ForestStatistics.KEY_BURNING_CELLS)).append('\n');
    }

    private static void appendWator(StringBuilder sb) {
        SimulationManager<?, ?, ?, ?> manager = new WatorSimulationManager(createWatorConfig());
        manager.executeSteps(STEP_COUNT, false, () -> {
        });
        var extrema = manager.statisticsExtrema();
        sb.append("=== Wator (seed=").append(SEED).append(", steps=").append(STEP_COUNT).append(") ===\n");
        sb.append("  minFishCells  = ").append(minimumValue(extrema, WatorStatistics.KEY_FISH_CELLS)).append('\n');
        sb.append("  maxFishCells  = ").append(maximumValue(extrema, WatorStatistics.KEY_FISH_CELLS)).append('\n');
        sb.append("  minSharkCells = ").append(minimumValue(extrema, WatorStatistics.KEY_SHARK_CELLS)).append('\n');
        sb.append("  maxSharkCells = ").append(maximumValue(extrema, WatorStatistics.KEY_SHARK_CELLS)).append('\n');
    }

}
