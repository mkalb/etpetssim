package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import static de.mkalb.etpetssim.simulations.wator.model.WatorConstraints.*;

/**
 * Immutable configuration for a simulation.
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

    private boolean hasValidRanges() {
        return isInRangeDouble(fishPercent, FISH_PERCENT_MIN, FISH_PERCENT_MAX)
                && isInRangeDouble(sharkPercent, SHARK_PERCENT_MIN, SHARK_PERCENT_MAX)
                && isInRangeInt(fishMaxAge, FISH_MAX_AGE_MIN, FISH_MAX_AGE_MAX)
                && isInRangeInt(fishMinReproductionAge, FISH_MIN_REPRODUCTION_AGE_MIN, FISH_MIN_REPRODUCTION_AGE_MAX)
                && isInRangeInt(fishMinReproductionInterval, FISH_MIN_REPRODUCTION_INTERVAL_MIN, FISH_MIN_REPRODUCTION_INTERVAL_MAX)
                && isInRangeInt(sharkMaxAge, SHARK_MAX_AGE_MIN, SHARK_MAX_AGE_MAX)
                && isInRangeInt(sharkBirthEnergy, SHARK_BIRTH_ENERGY_MIN, SHARK_BIRTH_ENERGY_MAX)
                && isInRangeInt(sharkEnergyLossPerStep, SHARK_ENERGY_LOSS_PER_STEP_MIN, SHARK_ENERGY_LOSS_PER_STEP_MAX)
                && isInRangeInt(sharkEnergyGainPerFish, SHARK_ENERGY_GAIN_PER_FISH_MIN, SHARK_ENERGY_GAIN_PER_FISH_MAX)
                && isInRangeInt(sharkMinReproductionAge, SHARK_MIN_REPRODUCTION_AGE_MIN, SHARK_MIN_REPRODUCTION_AGE_MAX)
                && isInRangeInt(sharkMinReproductionEnergy, SHARK_MIN_REPRODUCTION_ENERGY_MIN, SHARK_MIN_REPRODUCTION_ENERGY_MAX)
                && isInRangeInt(sharkMinReproductionInterval, SHARK_MIN_REPRODUCTION_INTERVAL_MIN, SHARK_MIN_REPRODUCTION_INTERVAL_MAX);
    }

    private boolean hasValidCombinedPopulationShare() {
        return (fishPercent + sharkPercent) <= POPULATION_SHARE_SUM_MAX_INCLUSIVE;
    }

    /**
     * Validates the common simulation settings and ensures that fish and shark shares fit into the grid.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && hasExpectedSelection(neighborhoodMode, NEIGHBORHOOD_MODE_DEFAULT)
                && hasValidRanges()
                && hasValidCombinedPopulationShare();
    }

}
