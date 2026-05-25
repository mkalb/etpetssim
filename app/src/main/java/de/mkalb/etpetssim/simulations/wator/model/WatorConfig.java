package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

import static de.mkalb.etpetssim.simulations.wator.model.WatorConstraints.*;

/**
 * Immutable configuration for the Wa-Tor simulation.
 *
 * @param cellShape                    the configured cell shape
 * @param gridEdgeBehavior             the configured grid edge behavior
 * @param gridWidth                    the grid width in cells
 * @param gridHeight                   the grid height in cells
 * @param cellEdgeLength               the rendered cell edge length in pixels
 * @param cellDisplayMode              the cell display mode used by the UI
 * @param seed                         the random seed used for initialization
 * @param fishPercent                  the initial fish population share
 * @param sharkPercent                 the initial shark population share
 * @param neighborhoodMode             the neighborhood mode used for movement and interaction
 * @param fishMaxAge                   the maximum fish age
 * @param fishMinReproductionAge       the minimum fish age for reproduction
 * @param fishMinReproductionInterval  the minimum number of steps between fish reproductions
 * @param sharkMaxAge                  the maximum shark age
 * @param sharkBirthEnergy             the initial shark energy
 * @param sharkEnergyLossPerStep       the shark energy loss per step
 * @param sharkEnergyGainPerFish       the shark energy gained by eating one fish
 * @param sharkMinReproductionAge      the minimum shark age for reproduction
 * @param sharkMinReproductionEnergy   the minimum shark energy for reproduction
 * @param sharkMinReproductionInterval the minimum number of steps between shark reproductions
 */
public record WatorConfig(
        // Structure
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        // Layout
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        // Initialization
        long seed,
        double fishPercent,
        double sharkPercent,
        // Rules
        NeighborhoodMode neighborhoodMode,
        int fishMaxAge,
        int fishMinReproductionAge,
        int fishMinReproductionInterval,
        int sharkMaxAge,
        int sharkBirthEnergy,
        int sharkEnergyLossPerStep,
        int sharkEnergyGainPerFish,
        int sharkMinReproductionAge,
        int sharkMinReproductionEnergy,
        int sharkMinReproductionInterval)
        implements SimulationConfig {

    private boolean hasAllowedSelections() {
        return CELL_SHAPE_VALUES.contains(cellShape)
                && GRID_EDGE_BEHAVIOR_VALUES.contains(gridEdgeBehavior)
                && CELL_DISPLAY_MODE_VALUES.contains(cellDisplayMode);
    }

    private boolean hasExpectedRules() {
        return neighborhoodMode == NEIGHBORHOOD_MODE_DEFAULT;
    }

    private boolean hasValidRanges() {
        return isInRange(fishPercent, FISH_PERCENT_MIN, FISH_PERCENT_MAX)
                && isInRange(sharkPercent, SHARK_PERCENT_MIN, SHARK_PERCENT_MAX)
                && isInRange(fishMaxAge, FISH_MAX_AGE_MIN, FISH_MAX_AGE_MAX)
                && isInRange(fishMinReproductionAge, FISH_MIN_REPRODUCTION_AGE_MIN, FISH_MIN_REPRODUCTION_AGE_MAX)
                && isInRange(fishMinReproductionInterval, FISH_MIN_REPRODUCTION_INTERVAL_MIN, FISH_MIN_REPRODUCTION_INTERVAL_MAX)
                && isInRange(sharkMaxAge, SHARK_MAX_AGE_MIN, SHARK_MAX_AGE_MAX)
                && isInRange(sharkBirthEnergy, SHARK_BIRTH_ENERGY_MIN, SHARK_BIRTH_ENERGY_MAX)
                && isInRange(sharkEnergyLossPerStep, SHARK_ENERGY_LOSS_PER_STEP_MIN, SHARK_ENERGY_LOSS_PER_STEP_MAX)
                && isInRange(sharkEnergyGainPerFish, SHARK_ENERGY_GAIN_PER_FISH_MIN, SHARK_ENERGY_GAIN_PER_FISH_MAX)
                && isInRange(sharkMinReproductionAge, SHARK_MIN_REPRODUCTION_AGE_MIN, SHARK_MIN_REPRODUCTION_AGE_MAX)
                && isInRange(sharkMinReproductionEnergy, SHARK_MIN_REPRODUCTION_ENERGY_MIN, SHARK_MIN_REPRODUCTION_ENERGY_MAX)
                && isInRange(sharkMinReproductionInterval, SHARK_MIN_REPRODUCTION_INTERVAL_MIN, SHARK_MIN_REPRODUCTION_INTERVAL_MAX);
    }

    private boolean hasValidCombinedPopulationShare() {
        return (fishPercent + sharkPercent) < POPULATION_SHARE_SUM_MAX_EXCLUSIVE;
    }

    /**
     * Validates the common simulation settings and ensures that fish and shark shares fit into the grid.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return SimulationConfig.super.isValid()
                && hasAllowedSelections()
                && hasExpectedRules()
                && hasValidRanges()
                && hasValidCombinedPopulationShare();
    }

}
