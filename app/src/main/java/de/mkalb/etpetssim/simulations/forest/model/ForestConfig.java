package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

import static de.mkalb.etpetssim.simulations.forest.model.ForestConstraints.*;

/**
 * Immutable configuration for the forest-fire simulation.
 *
 * @param cellShape the configured cell shape
 * @param gridEdgeBehavior the configured grid edge behavior
 * @param gridWidth the grid width in cells
 * @param gridHeight the grid height in cells
 * @param cellEdgeLength the rendered cell edge length in pixels
 * @param cellDisplayMode the cell display mode used by the UI
 * @param seed the random seed used for initialization
 * @param treeDensity the initial tree density
 * @param treeGrowthProbability the probability that an empty cell grows a tree
 * @param lightningIgnitionProbability the probability that a tree ignites spontaneously
 * @param neighborhoodMode the neighborhood mode used during updates
 */
public record ForestConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        double treeDensity,
        double treeGrowthProbability,
        double lightningIgnitionProbability,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    private boolean hasAllowedSelections() {
        return CELL_SHAPE_VALUES.contains(cellShape)
                && GRID_EDGE_BEHAVIOR_VALUES.contains(gridEdgeBehavior)
                && CELL_DISPLAY_MODE_VALUES.contains(cellDisplayMode)
                && NEIGHBORHOOD_MODE_VALUES.contains(neighborhoodMode);
    }

    private boolean hasValidRanges() {
        return isInRange(treeDensity, TREE_DENSITY_MIN, TREE_DENSITY_MAX)
                && isInRange(treeGrowthProbability, TREE_GROWTH_PROBABILITY_MIN, TREE_GROWTH_PROBABILITY_MAX)
                && isInRange(lightningIgnitionProbability, LIGHTNING_IGNITION_PROBABILITY_MIN, LIGHTNING_IGNITION_PROBABILITY_MAX);
    }

    /**
     * Validates the common simulation settings and the forest-specific supported selections and probability ranges.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return SimulationConfig.super.isValid()
                && hasAllowedSelections()
                && hasValidRanges();
    }

}
