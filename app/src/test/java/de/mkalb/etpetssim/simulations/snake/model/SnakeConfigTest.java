package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.snake.shared.SnakeDeathMode;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.BASE_POINTS_PER_FOOD_MAX;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.BASE_POINTS_PER_FOOD_MIN;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.CELL_EDGE_LENGTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.CELL_SHAPE_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.DEATH_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.FOOD_CELLS_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.FOOD_CELLS_MAX;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.FOOD_CELLS_MIN;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.GRID_HEIGHT_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.GRID_WIDTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.GROWTH_PER_FOOD_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.GROWTH_PER_FOOD_MAX;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.GROWTH_PER_FOOD_MIN;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.INITIAL_PENDING_GROWTH_MAX;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.INITIAL_PENDING_GROWTH_MIN;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_MAX;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_MIN;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.SNAKES_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.SNAKES_MAX;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.SNAKES_MIN;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.VERTICAL_WALLS_DEFAULT;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.VERTICAL_WALLS_MAX;
import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.VERTICAL_WALLS_MIN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SnakeConfigTest {

    private static boolean isValidWith(Consumer<ConfigValues> change) {
        ConfigValues values = new ConfigValues();
        change.accept(values);
        return values.toConfig().isValid();
    }

    @Test
    void testIsValidAcceptsDefaultConstraints() {
        assertTrue(isValidWith(_ -> {}));
    }

    @Test
    void testIsValidAcceptsSupportedAlternativeSelections() {
        assertTrue(isValidWith(v -> {
            v.cellShape = CellShape.SQUARE;
            v.gridEdgeBehavior = GridEdgeBehavior.BLOCK_XY;
            v.deathMode = SnakeDeathMode.PERMADEATH;
        }));
    }

    @Test
    void testIsValidAcceptsBoundaryValues() {
        assertAll(
                () -> assertTrue(isValidWith(v -> v.verticalWalls = VERTICAL_WALLS_MIN)),
                () -> assertTrue(isValidWith(v -> v.verticalWalls = VERTICAL_WALLS_MAX)),
                () -> assertTrue(isValidWith(v -> v.foodCells = FOOD_CELLS_MIN)),
                () -> assertTrue(isValidWith(v -> v.foodCells = FOOD_CELLS_MAX)),
                () -> assertTrue(isValidWith(v -> v.snakes = SNAKES_MIN)),
                () -> assertTrue(isValidWith(v -> v.snakes = SNAKES_MAX)),
                () -> assertTrue(isValidWith(v -> v.initialPendingGrowth = INITIAL_PENDING_GROWTH_MIN)),
                () -> assertTrue(isValidWith(v -> v.initialPendingGrowth = INITIAL_PENDING_GROWTH_MAX)),
                () -> assertTrue(isValidWith(v -> v.growthPerFood = GROWTH_PER_FOOD_MIN)),
                () -> assertTrue(isValidWith(v -> v.growthPerFood = GROWTH_PER_FOOD_MAX)),
                () -> assertTrue(isValidWith(v -> v.basePointsPerFood = BASE_POINTS_PER_FOOD_MIN)),
                () -> assertTrue(isValidWith(v -> v.basePointsPerFood = BASE_POINTS_PER_FOOD_MAX)),
                () -> assertTrue(isValidWith(v -> v.segmentLengthMultiplier = SEGMENT_LENGTH_MULTIPLIER_MIN)),
                () -> assertTrue(isValidWith(v -> v.segmentLengthMultiplier = SEGMENT_LENGTH_MULTIPLIER_MAX))
        );
    }

    @Test
    void testIsValidRejectsUnsupportedCellShape() {
        assertFalse(isValidWith(v -> v.cellShape = CellShape.TRIANGLE));
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        assertFalse(isValidWith(v -> v.gridEdgeBehavior = GridEdgeBehavior.ABSORB_XY));
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        assertFalse(isValidWith(v -> v.cellDisplayMode = CellDisplayMode.SHAPE));
    }

    @Test
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        assertFalse(isValidWith(v -> v.neighborhoodMode = NeighborhoodMode.EDGES_AND_VERTICES));
    }

    @Test
    void testIsValidRejectsVerticalWallsOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.verticalWalls = VERTICAL_WALLS_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.verticalWalls = VERTICAL_WALLS_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsFoodCellsOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.foodCells = FOOD_CELLS_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.foodCells = FOOD_CELLS_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSnakesOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.snakes = SNAKES_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.snakes = SNAKES_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsInitialPendingGrowthOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.initialPendingGrowth = INITIAL_PENDING_GROWTH_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.initialPendingGrowth = INITIAL_PENDING_GROWTH_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsGrowthPerFoodOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.growthPerFood = GROWTH_PER_FOOD_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.growthPerFood = GROWTH_PER_FOOD_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsBasePointsPerFoodOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.basePointsPerFood = BASE_POINTS_PER_FOOD_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.basePointsPerFood = BASE_POINTS_PER_FOOD_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSegmentLengthMultiplierOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(
                        v -> v.segmentLengthMultiplier = Math.nextDown(SEGMENT_LENGTH_MULTIPLIER_MIN))),
                () -> assertFalse(isValidWith(
                        v -> v.segmentLengthMultiplier = Math.nextUp(SEGMENT_LENGTH_MULTIPLIER_MAX)))
        );
    }

    private static final class ConfigValues {

        CellShape cellShape = CELL_SHAPE_DEFAULT;
        GridEdgeBehavior gridEdgeBehavior = GRID_EDGE_BEHAVIOR_DEFAULT;
        CellDisplayMode cellDisplayMode = CELL_DISPLAY_MODE_DEFAULT;
        int verticalWalls = VERTICAL_WALLS_DEFAULT;
        int foodCells = FOOD_CELLS_DEFAULT;
        int snakes = SNAKES_DEFAULT;
        int initialPendingGrowth = INITIAL_PENDING_GROWTH_DEFAULT;
        NeighborhoodMode neighborhoodMode = NEIGHBORHOOD_MODE_DEFAULT;
        SnakeDeathMode deathMode = DEATH_MODE_DEFAULT;
        int growthPerFood = GROWTH_PER_FOOD_DEFAULT;
        int basePointsPerFood = BASE_POINTS_PER_FOOD_DEFAULT;
        double segmentLengthMultiplier = SEGMENT_LENGTH_MULTIPLIER_DEFAULT;

        SnakeConfig toConfig() {
            return new SnakeConfig(cellShape, gridEdgeBehavior, GRID_WIDTH_DEFAULT, GRID_HEIGHT_DEFAULT,
                    CELL_EDGE_LENGTH_DEFAULT, cellDisplayMode, 1L, verticalWalls, foodCells, snakes,
                    initialPendingGrowth, neighborhoodMode, deathMode, growthPerFood, basePointsPerFood,
                    segmentLengthMultiplier);
        }

    }

}
