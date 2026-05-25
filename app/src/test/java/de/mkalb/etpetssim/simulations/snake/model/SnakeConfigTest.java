package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SnakeConfigTest {

    private static SnakeConfig createConfig(CellShape cellShape,
                                            GridEdgeBehavior gridEdgeBehavior,
                                            CellDisplayMode cellDisplayMode,
                                            int verticalWalls,
                                            int foodCells,
                                            int snakes,
                                            int initialPendingGrowth,
                                            SnakeDeathMode deathMode,
                                            int growthPerFood,
                                            int basePointsPerFood,
                                            double segmentLengthMultiplier,
                                            NeighborhoodMode neighborhoodMode) {
        return new SnakeConfig(
                cellShape,
                gridEdgeBehavior,
                SnakeConstraints.GRID_WIDTH_DEFAULT,
                SnakeConstraints.GRID_HEIGHT_DEFAULT,
                SnakeConstraints.CELL_EDGE_LENGTH_DEFAULT,
                cellDisplayMode,
                1L,
                verticalWalls,
                foodCells,
                snakes,
                initialPendingGrowth,
                deathMode,
                growthPerFood,
                basePointsPerFood,
                segmentLengthMultiplier,
                neighborhoodMode
        );
    }

    @Test
    void testIsValidWithDefaultConstraints() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedCellShape() {
        SnakeConfig config = createConfig(
                CellShape.TRIANGLE,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                CellDisplayMode.SHAPE,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                NeighborhoodMode.EDGES_AND_VERTICES);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsVerticalWallsOutsideRange() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_MAX + 1,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsFoodCellsOutsideRange() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_MAX + 1,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsSnakesOutsideRange() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_MAX + 1,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsInitialPendingGrowthOutsideRange() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_MAX + 1,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsGrowthPerFoodOutsideRange() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_MAX + 1,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsBasePointsPerFoodOutsideRange() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_MAX + 1,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsSegmentLengthMultiplierOutsideRange() {
        SnakeConfig config = createConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                Math.nextUp(SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_MAX),
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidAcceptsSupportedAlternativeSelections() {
        SnakeConfig config = createConfig(
                CellShape.SQUARE,
                GridEdgeBehavior.BLOCK_XY,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SnakeConstraints.VERTICAL_WALLS_DEFAULT,
                SnakeConstraints.FOOD_CELLS_DEFAULT,
                SnakeConstraints.SNAKES_DEFAULT,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeDeathMode.PERMADEATH,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

}
