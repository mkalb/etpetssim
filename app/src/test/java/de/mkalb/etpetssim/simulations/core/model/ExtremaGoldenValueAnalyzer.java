package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.forest.model.*;
import de.mkalb.etpetssim.simulations.wator.model.*;

/**
 * Manual utility to re-capture extrema golden values for Conway, Forest, and Wator
 * using seed 1L and 20 steps. Run once when simulation logic changes to refresh the
 * hard-coded expectations in TimedStatisticsTrackingTest (testConway/Forest/WatorGoldenExtremaExactValues).
 *
 * <p>Current golden values (seed=1, 20 steps, default constraints):
 * <pre>
 *   Conway  — minAliveCells   : 3575 | maxAliveCells   : 6945
 *             minChangedCells : 0    | maxChangedCells : 6475
 *   Forest  — maxEmptyCells   : 4000
 *             minTreeCells    : 1000 | maxTreeCells    : 1037 | maxBurningCells : 17
 *   Wator   — minFishCells    : 1900 | maxFishCells    : 6127
 *             minSharkCells   : 1000 | maxSharkCells   : 3002
 * </pre>
 */
@SuppressWarnings({"MagicNumber", "HardcodedLineSeparator"})
public final class ExtremaGoldenValueAnalyzer {

    private ExtremaGoldenValueAnalyzer() {
    }

    static void main() {
        System.out.println(captureValues());
    }

    static String captureValues() {
        var sb = new StringBuilder(512);
        appendConway(sb);
        appendForest(sb);
        appendWator(sb);
        return sb.toString();
    }

    private static ConwayConfig conwayConfig() {
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

    private static ForestConfig forestConfig() {
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

    private static WatorConfig watorConfig() {
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

    private static void appendConway(StringBuilder sb) {
        ConwaySimulationManager m = new ConwaySimulationManager(conwayConfig());
        m.executeSteps(20, false, () -> {
        });
        var extrema = m.statisticsExtrema();
        sb.append("=== Conway (seed=1, steps=20) ===\n");
        sb.append("  minAliveCells    = ").append(extrema.minimumValues().get(ConwayStatistics.KEY_ALIVE_CELLS).intValue()).append('\n');
        sb.append("  maxAliveCells    = ").append(extrema.maximumValues().get(ConwayStatistics.KEY_ALIVE_CELLS).intValue()).append('\n');
        sb.append("  minChangedCells  = ").append(extrema.minimumValues().get(ConwayStatistics.KEY_CHANGED_CELLS).intValue()).append('\n');
        sb.append("  maxChangedCells  = ").append(extrema.maximumValues().get(ConwayStatistics.KEY_CHANGED_CELLS).intValue()).append('\n');
    }

    private static void appendForest(StringBuilder sb) {
        ForestSimulationManager m = new ForestSimulationManager(forestConfig());
        m.executeSteps(20, false, () -> {
        });
        var extrema = m.statisticsExtrema();
        sb.append("=== Forest (seed=1, steps=20) ===\n");
        sb.append("  maxEmptyCells   = ").append(extrema.maximumValues().get(ForestStatistics.KEY_EMPTY_CELLS).intValue()).append('\n');
        sb.append("  minTreeCells    = ").append(extrema.minimumValues().get(ForestStatistics.KEY_TREE_CELLS).intValue()).append('\n');
        sb.append("  maxTreeCells    = ").append(extrema.maximumValues().get(ForestStatistics.KEY_TREE_CELLS).intValue()).append('\n');
        sb.append("  maxBurningCells = ").append(extrema.maximumValues().get(ForestStatistics.KEY_BURNING_CELLS).intValue()).append('\n');
    }

    private static void appendWator(StringBuilder sb) {
        WatorSimulationManager m = new WatorSimulationManager(watorConfig());
        m.executeSteps(20, false, () -> {
        });
        var extrema = m.statisticsExtrema();
        sb.append("=== Wator (seed=1, steps=20) ===\n");
        sb.append("  minFishCells  = ").append(extrema.minimumValues().get(WatorStatistics.KEY_FISH_CELLS).intValue()).append('\n');
        sb.append("  maxFishCells  = ").append(extrema.maximumValues().get(WatorStatistics.KEY_FISH_CELLS).intValue()).append('\n');
        sb.append("  minSharkCells = ").append(extrema.minimumValues().get(WatorStatistics.KEY_SHARK_CELLS).intValue()).append('\n');
        sb.append("  maxSharkCells = ").append(extrema.maximumValues().get(WatorStatistics.KEY_SHARK_CELLS).intValue()).append('\n');
    }

}
