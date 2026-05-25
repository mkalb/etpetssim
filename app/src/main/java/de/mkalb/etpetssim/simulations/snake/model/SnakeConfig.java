package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.*;

/**
 * Immutable configuration for the snake simulation.
 *
 * @param cellShape               the configured cell shape
 * @param gridEdgeBehavior        the configured grid edge behavior
 * @param gridWidth               the grid width in cells
 * @param gridHeight              the grid height in cells
 * @param cellEdgeLength          the rendered cell edge length in pixels
 * @param cellDisplayMode         the cell display mode used by the UI
 * @param seed                    the random seed used for initialization
 * @param verticalWalls           the number of generated vertical wall segments
 * @param foodCells               the number of food cells to place initially
 * @param snakes                  the number of snakes to create
 * @param initialPendingGrowth    the pending growth assigned to newly spawned snakes
 * @param neighborhoodMode        the neighborhood mode used for snake movement
 * @param deathMode               the rule that determines how snakes die
 * @param growthPerFood           the growth awarded per eaten food cell
 * @param basePointsPerFood       the base score awarded per eaten food cell
 * @param segmentLengthMultiplier the multiplier used for score scaling by snake length
 */
public record SnakeConfig(
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
        int verticalWalls,
        int foodCells,
        int snakes,
        int initialPendingGrowth,
        // Rules
        NeighborhoodMode neighborhoodMode,
        SnakeDeathMode deathMode,
        int growthPerFood,
        int basePointsPerFood,
        double segmentLengthMultiplier)
        implements SimulationConfig {

    private boolean hasAllowedSelections() {
        return CELL_SHAPE_VALUES.contains(cellShape)
                && GRID_EDGE_BEHAVIOR_VALUES.contains(gridEdgeBehavior)
                && CELL_DISPLAY_MODE_VALUES.contains(cellDisplayMode)
                && SNAKE_DEATH_MODE_VALUES.contains(deathMode);
    }

    private boolean hasExpectedRules() {
        return neighborhoodMode == NEIGHBORHOOD_MODE_DEFAULT;
    }

    private boolean hasValidRanges() {
        return isInRange(verticalWalls, VERTICAL_WALLS_MIN, VERTICAL_WALLS_MAX)
                && isInRange(foodCells, FOOD_CELLS_MIN, FOOD_CELLS_MAX)
                && isInRange(snakes, SNAKES_MIN, SNAKES_MAX)
                && isInRange(initialPendingGrowth, INITIAL_PENDING_GROWTH_MIN, INITIAL_PENDING_GROWTH_MAX)
                && isInRange(growthPerFood, GROWTH_PER_FOOD_MIN, GROWTH_PER_FOOD_MAX)
                && isInRange(basePointsPerFood, BASE_POINTS_PER_FOOD_MIN, BASE_POINTS_PER_FOOD_MAX)
                && isInRange(segmentLengthMultiplier, SEGMENT_LENGTH_MULTIPLIER_MIN, SEGMENT_LENGTH_MULTIPLIER_MAX);
    }

    /**
     * Validates the common simulation settings and the snake-specific supported selections and ranges.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return SimulationConfig.super.isValid()
                && hasAllowedSelections()
                && hasExpectedRules()
                && hasValidRanges();
    }

}
