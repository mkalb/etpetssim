package de.mkalb.etpetssim.simulations.snake.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import de.mkalb.etpetssim.simulations.snake.model.SnakeDeathMode;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class SnakeConfigViewModel
        extends AbstractConfigViewModel<SnakeConfig> {

    public static final int SEGMENT_LENGTH_MULTIPLIER_DECIMALS = 1;

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.HEXAGON,
            List.of(CellShape.SQUARE, CellShape.HEXAGON),
            GridEdgeBehavior.WRAP_X_BLOCK_Y,
            List.of(GridEdgeBehavior.BLOCK_XY,
                    GridEdgeBehavior.WRAP_XY,
                    GridEdgeBehavior.BLOCK_X_WRAP_Y,
                    GridEdgeBehavior.WRAP_X_BLOCK_Y),
            80,
            GridSize.MIN_SIZE,
            1_000,
            Math.max(GridTopology.SQUARE_MAX_REQUIRED_WIDTH_MULTIPLE, GridTopology.HEXAGON_MAX_REQUIRED_WIDTH_MULTIPLE),
            40,
            GridSize.MIN_SIZE,
            1_000,
            Math.max(GridTopology.SQUARE_MAX_REQUIRED_HEIGHT_MULTIPLE, GridTopology.HEXAGON_MAX_REQUIRED_HEIGHT_MULTIPLE),
            6,
            1,
            50,
            CellDisplayMode.SHAPE_BORDERED,
            List.of(CellDisplayMode.SHAPE_BORDERED),
            ""
    );

    // Initialization
    private static final int VERTICAL_WALLS_INITIAL = 6;
    private static final int VERTICAL_WALLS_MIN = 0;
    private static final int VERTICAL_WALLS_MAX = 100;
    private static final int VERTICAL_WALLS_STEP = 1;
    private static final int FOOD_CELLS_INITIAL = 50;
    private static final int FOOD_CELLS_MIN = 0;
    private static final int FOOD_CELLS_MAX = 10_000;
    private static final int FOOD_CELLS_STEP = 1;
    private static final int SNAKES_INITIAL = 15;
    private static final int SNAKES_MIN = 0;
    private static final int SNAKES_MAX = 1_000;
    private static final int SNAKES_STEP = 1;
    private static final int INITIAL_PENDING_GROWTH_INITIAL = 2;
    private static final int INITIAL_PENDING_GROWTH_MIN = 0;
    private static final int INITIAL_PENDING_GROWTH_MAX = 1_000;
    private static final int INITIAL_PENDING_GROWTH_STEP = 1;

    // Rules
    private static final SnakeDeathMode SNAKE_DEATH_MODE_INITIAL = SnakeDeathMode.RESPAWN;
    private static final int GROWTH_PER_FOOD_INITIAL = 1;
    private static final int GROWTH_PER_FOOD_MIN = 0;
    private static final int GROWTH_PER_FOOD_MAX = 100;
    private static final int GROWTH_PER_FOOD_STEP = 1;
    private static final int BASE_POINTS_PER_FOOD_INITIAL = 10;
    private static final int BASE_POINTS_PER_FOOD_MIN = 0;
    private static final int BASE_POINTS_PER_FOOD_MAX = 100;
    private static final int BASE_POINTS_PER_FOOD_STEP = 1;
    private static final double SEGMENT_LENGTH_MULTIPLIER_INITIAL = 0.5d;
    private static final double SEGMENT_LENGTH_MULTIPLIER_MIN = 0.0d;
    private static final double SEGMENT_LENGTH_MULTIPLIER_MAX = 5.0d;
    private static final NeighborhoodMode NEIGHBORHOOD_MODE = NeighborhoodMode.EDGES_ONLY;

    // Initialization properties
    private final InputIntegerProperty verticalWalls = InputIntegerProperty.of(
            VERTICAL_WALLS_INITIAL,
            VERTICAL_WALLS_MIN,
            VERTICAL_WALLS_MAX,
            VERTICAL_WALLS_STEP);
    private final InputIntegerProperty foodCells = InputIntegerProperty.of(
            FOOD_CELLS_INITIAL,
            FOOD_CELLS_MIN,
            FOOD_CELLS_MAX,
            FOOD_CELLS_STEP);
    private final InputIntegerProperty snakes = InputIntegerProperty.of(
            SNAKES_INITIAL,
            SNAKES_MIN,
            SNAKES_MAX,
            SNAKES_STEP);
    private final InputIntegerProperty initialPendingGrowth = InputIntegerProperty.of(
            INITIAL_PENDING_GROWTH_INITIAL,
            INITIAL_PENDING_GROWTH_MIN,
            INITIAL_PENDING_GROWTH_MAX,
            INITIAL_PENDING_GROWTH_STEP);

    // Rules properties
    private final InputEnumProperty<SnakeDeathMode> deathMode = InputEnumProperty.of(
            SNAKE_DEATH_MODE_INITIAL,
            SnakeDeathMode.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final InputIntegerProperty growthPerFood = InputIntegerProperty.of(
            GROWTH_PER_FOOD_INITIAL,
            GROWTH_PER_FOOD_MIN,
            GROWTH_PER_FOOD_MAX,
            GROWTH_PER_FOOD_STEP);
    private final InputIntegerProperty basePointsPerFood = InputIntegerProperty.of(
            BASE_POINTS_PER_FOOD_INITIAL,
            BASE_POINTS_PER_FOOD_MIN,
            BASE_POINTS_PER_FOOD_MAX,
            BASE_POINTS_PER_FOOD_STEP);
    private final InputDoubleProperty segmentLengthMultiplier = InputDoubleProperty.of(
            SEGMENT_LENGTH_MULTIPLIER_INITIAL,
            SEGMENT_LENGTH_MULTIPLIER_MIN,
            SEGMENT_LENGTH_MULTIPLIER_MAX);

    public SnakeConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
    }

    @Override
    public SnakeConfig getConfig() {
        return new SnakeConfig(
                // Structure
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                // Initialization
                verticalWalls.getValue(),
                foodCells.getValue(),
                snakes.getValue(),
                initialPendingGrowth.getValue(),
                // Rules
                deathMode.getValue(),
                growthPerFood.getValue(),
                basePointsPerFood.getValue(),
                segmentLengthMultiplier.getValue(),
                NEIGHBORHOOD_MODE
        );
    }

    public InputIntegerProperty verticalWallsProperty() {
        return verticalWalls;
    }

    public InputIntegerProperty foodCellsProperty() {
        return foodCells;
    }

    public InputIntegerProperty snakesProperty() {
        return snakes;
    }

    public InputIntegerProperty initialPendingGrowthProperty() {
        return initialPendingGrowth;
    }

    public InputEnumProperty<SnakeDeathMode> deathModeProperty() {
        return deathMode;
    }

    public InputIntegerProperty growthPerFoodProperty() {
        return growthPerFood;
    }

    public InputIntegerProperty basePointsPerFoodProperty() {
        return basePointsPerFood;
    }

    public InputDoubleProperty segmentLengthMultiplierProperty() {
        return segmentLengthMultiplier;
    }

}
