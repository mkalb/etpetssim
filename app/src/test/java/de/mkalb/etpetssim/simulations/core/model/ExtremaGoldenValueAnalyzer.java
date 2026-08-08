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
 *   Conway  — maxAliveCells : 6945
 *   Forest  — maxTreeCells  : 1037 | maxBurningCells : 17
 *   Wator   — minFishCells  : 1900 | maxFishCells    : 6127
 *             minSharkCells : 1000 | maxSharkCells   : 3002
 * </pre>
 */
@SuppressWarnings("MagicNumber")
public final class ExtremaGoldenValueAnalyzer {

    private ExtremaGoldenValueAnalyzer() {
    }

    static void main() {
        printConway();
        printForest();
        printWator();
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

    private static void printConway() {
        ConwaySimulationManager m = new ConwaySimulationManager(conwayConfig());
        m.executeSteps(20, false, () -> {
        });
        var extrema = m.statisticsExtrema();
        System.out.println("=== Conway (seed=1, steps=20) ===");
        System.out.println("  maxAliveCells = " + extrema.maximumValues().get(ConwayStatistics.KEY_ALIVE_CELLS).intValue());
    }

    private static void printForest() {
        ForestSimulationManager m = new ForestSimulationManager(forestConfig());
        m.executeSteps(20, false, () -> {
        });
        var extrema = m.statisticsExtrema();
        System.out.println("=== Forest (seed=1, steps=20) ===");
        System.out.println("  maxTreeCells    = " + extrema.maximumValues().get(ForestStatistics.KEY_TREE_CELLS).intValue());
        System.out.println("  maxBurningCells = " + extrema.maximumValues().get(ForestStatistics.KEY_BURNING_CELLS).intValue());
    }

    private static void printWator() {
        WatorSimulationManager m = new WatorSimulationManager(watorConfig());
        m.executeSteps(20, false, () -> {
        });
        var extrema = m.statisticsExtrema();
        System.out.println("=== Wator (seed=1, steps=20) ===");
        System.out.println("  minFishCells  = " + extrema.minimumValues().get(WatorStatistics.KEY_FISH_CELLS).intValue());
        System.out.println("  maxFishCells  = " + extrema.maximumValues().get(WatorStatistics.KEY_FISH_CELLS).intValue());
        System.out.println("  minSharkCells = " + extrema.minimumValues().get(WatorStatistics.KEY_SHARK_CELLS).intValue());
        System.out.println("  maxSharkCells = " + extrema.maximumValues().get(WatorStatistics.KEY_SHARK_CELLS).intValue());
    }

}
