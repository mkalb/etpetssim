package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import static de.mkalb.etpetssim.simulations.forest.model.ForestConstraints.*;

/**
 * Immutable configuration of the Forest-fire model simulation.
 *
 * <p>The components are grouped into structure, layout, initialization, and rules. Instances are created
 * from unvalidated input; check them with {@link #isValid()} before passing them to the model.
 *
 * @param cellShape                    the configured cell shape
 * @param gridEdgeBehavior             the configured grid edge behavior
 * @param gridWidth                    the grid width in cells
 * @param gridHeight                   the grid height in cells
 * @param cellEdgeLength               the rendered cell edge length in pixels
 * @param cellDisplayMode              the cell display mode used by the UI
 * @param seed                         the random seed used for initialization
 * @param treeDensity                  the initial share of tree cells as a fraction of all cells
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
     * Validates the common simulation settings and the Forest-specific selections and ranges.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && hasValidRanges();
    }

}
