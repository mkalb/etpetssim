package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import static de.mkalb.etpetssim.simulations.forest.model.ForestConstraints.*;

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
 * @param treeDensity                  the initial tree density
 * @param neighborhoodMode             the neighborhood mode used during updates
 * @param treeGrowthProbability        the probability that an empty cell grows a tree
 * @param lightningIgnitionProbability the probability that a tree ignites spontaneously
 */
public record ForestConfig(
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
        double treeDensity,
        // Rules
        NeighborhoodMode neighborhoodMode,
        double treeGrowthProbability,
        double lightningIgnitionProbability)
        implements SimulationConfig {

    private boolean hasValidRanges() {
        return isInRangeDouble(treeDensity, TREE_DENSITY_MIN, TREE_DENSITY_MAX)
                && isInRangeDouble(treeGrowthProbability, TREE_GROWTH_PROBABILITY_MIN, TREE_GROWTH_PROBABILITY_MAX)
                && isInRangeDouble(lightningIgnitionProbability, LIGHTNING_IGNITION_PROBABILITY_MIN, LIGHTNING_IGNITION_PROBABILITY_MAX);
    }

    /**
     * Validates the common simulation settings and the forest-specific supported selections and probability ranges.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && isAllowedSelection(neighborhoodMode, NEIGHBORHOOD_MODE_VALUES)
                && hasValidRanges();
    }

}
