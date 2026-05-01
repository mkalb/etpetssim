package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

/**
 * Immutable configuration for the snake simulation.
 *
 * @param cellShape the configured cell shape
 * @param gridEdgeBehavior the configured grid edge behavior
 * @param gridWidth the grid width in cells
 * @param gridHeight the grid height in cells
 * @param cellEdgeLength the rendered cell edge length in pixels
 * @param cellDisplayMode the cell display mode used by the UI
 * @param seed the random seed used for initialization
 * @param verticalWalls the number of generated vertical wall segments
 * @param foodCells the number of food cells to place initially
 * @param snakes the number of snakes to create
 * @param initialPendingGrowth the pending growth assigned to newly spawned snakes
 * @param deathMode the rule that determines how snakes die
 * @param growthPerFood the growth awarded per eaten food cell
 * @param basePointsPerFood the base score awarded per eaten food cell
 * @param segmentLengthMultiplier the multiplier used for score scaling by snake length
 * @param neighborhoodMode the neighborhood mode used for snake movement
 */
public record SnakeConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        // Initialization
        int verticalWalls,
        int foodCells,
        int snakes,
        int initialPendingGrowth,
        // Rules
        SnakeDeathMode deathMode,
        int growthPerFood,
        int basePointsPerFood,
        double segmentLengthMultiplier,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {
}

