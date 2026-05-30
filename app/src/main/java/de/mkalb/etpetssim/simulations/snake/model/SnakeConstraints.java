package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.snake.shared.SnakeDeathMode;

import java.util.*;

/**
 * Configuration constraints and defaults used across the simulation layers.
 */
public final class SnakeConstraints {

    // Structure
    public static final CellShape CELL_SHAPE_DEFAULT = CellShape.HEXAGON;
    public static final List<CellShape> CELL_SHAPE_VALUES = List.of(
            CellShape.SQUARE,
            CellShape.HEXAGON);
    public static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_DEFAULT = GridEdgeBehavior.WRAP_X_BLOCK_Y;
    public static final List<GridEdgeBehavior> GRID_EDGE_BEHAVIOR_VALUES = List.of(
            GridEdgeBehavior.BLOCK_XY,
            GridEdgeBehavior.WRAP_XY,
            GridEdgeBehavior.BLOCK_X_WRAP_Y,
            GridEdgeBehavior.WRAP_X_BLOCK_Y);
    public static final int GRID_WIDTH_DEFAULT = 80;
    public static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    public static final int GRID_WIDTH_MAX = 1_000;
    public static final int GRID_WIDTH_STEP = Math.max(GridTopology.SQUARE_MAX_REQUIRED_WIDTH_MULTIPLE, GridTopology.HEXAGON_MAX_REQUIRED_WIDTH_MULTIPLE);
    public static final int GRID_HEIGHT_DEFAULT = 40;
    public static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    public static final int GRID_HEIGHT_MAX = 1_000;
    public static final int GRID_HEIGHT_STEP = Math.max(GridTopology.SQUARE_MAX_REQUIRED_HEIGHT_MULTIPLE, GridTopology.HEXAGON_MAX_REQUIRED_HEIGHT_MULTIPLE);

    // Layout
    public static final int CELL_EDGE_LENGTH_DEFAULT = 6;
    public static final int CELL_EDGE_LENGTH_MIN = 1;
    public static final int CELL_EDGE_LENGTH_MAX = 50;
    public static final CellDisplayMode CELL_DISPLAY_MODE_DEFAULT = CellDisplayMode.SHAPE_BORDERED;
    public static final List<CellDisplayMode> CELL_DISPLAY_MODE_VALUES = List.of(
            CellDisplayMode.SHAPE_BORDERED);

    // Initialization - Seed
    public static final String SEED_INITIAL = "";

    // Initialization
    public static final int RULE_STEP = 1;
    public static final int VERTICAL_WALLS_DEFAULT = 6;
    public static final int VERTICAL_WALLS_MIN = 0;
    public static final int VERTICAL_WALLS_MAX = 100;
    public static final int VERTICAL_WALLS_STEP = RULE_STEP;
    public static final int FOOD_CELLS_DEFAULT = 50;
    public static final int FOOD_CELLS_MIN = 0;
    public static final int FOOD_CELLS_MAX = 10_000;
    public static final int FOOD_CELLS_STEP = RULE_STEP;
    public static final int SNAKES_DEFAULT = 15;
    public static final int SNAKES_MIN = 0;
    public static final int SNAKES_MAX = 1_000;
    public static final int SNAKES_STEP = RULE_STEP;
    public static final int INITIAL_PENDING_GROWTH_DEFAULT = 2;
    public static final int INITIAL_PENDING_GROWTH_MIN = 0;
    public static final int INITIAL_PENDING_GROWTH_MAX = 1_000;
    public static final int INITIAL_PENDING_GROWTH_STEP = RULE_STEP;

    // Rules - NeighborhoodMode
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_ONLY;

    // Rules
    public static final SnakeDeathMode SNAKE_DEATH_MODE_DEFAULT = SnakeDeathMode.RESPAWN;
    public static final List<SnakeDeathMode> SNAKE_DEATH_MODE_VALUES = List.of(
            SnakeDeathMode.values());
    public static final int GROWTH_PER_FOOD_DEFAULT = 1;
    public static final int GROWTH_PER_FOOD_MIN = 0;
    public static final int GROWTH_PER_FOOD_MAX = 100;
    public static final int GROWTH_PER_FOOD_STEP = RULE_STEP;
    public static final int BASE_POINTS_PER_FOOD_DEFAULT = 10;
    public static final int BASE_POINTS_PER_FOOD_MIN = 0;
    public static final int BASE_POINTS_PER_FOOD_MAX = 100;
    public static final int BASE_POINTS_PER_FOOD_STEP = RULE_STEP;
    public static final double SEGMENT_LENGTH_MULTIPLIER_DEFAULT = 0.5d;
    public static final double SEGMENT_LENGTH_MULTIPLIER_MIN = 0.0d;
    public static final double SEGMENT_LENGTH_MULTIPLIER_MAX = 5.0d;

    /**
     * Private constructor to prevent instantiation.
     */
    private SnakeConstraints() {
    }

}
