package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

/**
 * Immutable configuration for the Sugarscape simulation.
 *
 * @param cellShape the configured cell shape
 * @param gridEdgeBehavior the configured grid edge behavior
 * @param gridWidth the grid width in cells
 * @param gridHeight the grid height in cells
 * @param cellEdgeLength the rendered cell edge length in pixels
 * @param cellDisplayMode the cell display mode used by the UI
 * @param seed the random seed used for initialization
 * @param agentPercent the initial percentage of agent cells
 * @param sugarPeaks the number of sugar peaks to generate
 * @param sugarRadiusLimit the maximum spread radius of a sugar peak
 * @param minSugarAmount the minimum sugar amount per cell
 * @param maxSugarAmount the maximum sugar amount per cell
 * @param agentInitialEnergy the initial energy assigned to new agents
 * @param sugarRegenerationRate the sugar amount regenerated per step
 * @param agentMetabolismRate the energy consumed by an agent per step
 * @param agentVisionRange the agent vision range
 * @param agentMaxAge the maximum agent age
 * @param neighborhoodMode the neighborhood mode used for movement and search
 */
public record SugarConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        // Initialization
        double agentPercent,
        int sugarPeaks,
        int sugarRadiusLimit,
        int minSugarAmount,
        int maxSugarAmount,
        int agentInitialEnergy,
        // Rules
        int sugarRegenerationRate,
        int agentMetabolismRate,
        int agentVisionRange,
        int agentMaxAge,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {
}
