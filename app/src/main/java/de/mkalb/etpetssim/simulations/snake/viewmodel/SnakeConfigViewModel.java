package de.mkalb.etpetssim.simulations.snake.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import de.mkalb.etpetssim.simulations.snake.shared.SnakeDeathMode;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.property.ReadOnlyObjectProperty;

import static de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints.*;

public final class SnakeConfigViewModel
        extends AbstractConfigViewModel<SnakeConfig> {

    /**
     * Number of fractional digits shown for segment-length multiplier inputs in the snake view.
     */
    public static final int SEGMENT_LENGTH_MULTIPLIER_DECIMALS = 1;

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CELL_SHAPE_DEFAULT,
            CELL_SHAPE_VALUES,
            GRID_EDGE_BEHAVIOR_DEFAULT,
            GRID_EDGE_BEHAVIOR_VALUES,
            GRID_WIDTH_DEFAULT,
            GRID_WIDTH_MIN,
            GRID_WIDTH_MAX,
            GRID_WIDTH_STEP,
            GRID_HEIGHT_DEFAULT,
            GRID_HEIGHT_MIN,
            GRID_HEIGHT_MAX,
            GRID_HEIGHT_STEP,
            CELL_EDGE_LENGTH_DEFAULT,
            CELL_EDGE_LENGTH_MIN,
            CELL_EDGE_LENGTH_MAX,
            CELL_DISPLAY_MODE_DEFAULT,
            CELL_DISPLAY_MODE_VALUES,
            SEED_INITIAL
    );

    // Initialization
    private final InputIntegerProperty verticalWalls = InputIntegerProperty.of(
            VERTICAL_WALLS_DEFAULT,
            VERTICAL_WALLS_MIN,
            VERTICAL_WALLS_MAX,
            VERTICAL_WALLS_STEP);
    private final InputIntegerProperty foodCells = InputIntegerProperty.of(
            FOOD_CELLS_DEFAULT,
            FOOD_CELLS_MIN,
            FOOD_CELLS_MAX,
            FOOD_CELLS_STEP);
    private final InputIntegerProperty snakes = InputIntegerProperty.of(
            SNAKES_DEFAULT,
            SNAKES_MIN,
            SNAKES_MAX,
            SNAKES_STEP);
    private final InputIntegerProperty initialPendingGrowth = InputIntegerProperty.of(
            INITIAL_PENDING_GROWTH_DEFAULT,
            INITIAL_PENDING_GROWTH_MIN,
            INITIAL_PENDING_GROWTH_MAX,
            INITIAL_PENDING_GROWTH_STEP);

    // Rules
    private final InputEnumProperty<SnakeDeathMode> deathMode = InputEnumProperty.of(
            SNAKE_DEATH_MODE_DEFAULT,
            SNAKE_DEATH_MODE_VALUES,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final InputIntegerProperty growthPerFood = InputIntegerProperty.of(
            GROWTH_PER_FOOD_DEFAULT,
            GROWTH_PER_FOOD_MIN,
            GROWTH_PER_FOOD_MAX,
            GROWTH_PER_FOOD_STEP);
    private final InputIntegerProperty basePointsPerFood = InputIntegerProperty.of(
            BASE_POINTS_PER_FOOD_DEFAULT,
            BASE_POINTS_PER_FOOD_MIN,
            BASE_POINTS_PER_FOOD_MAX,
            BASE_POINTS_PER_FOOD_STEP);
    private final InputDoubleProperty segmentLengthMultiplier = InputDoubleProperty.of(
            SEGMENT_LENGTH_MULTIPLIER_DEFAULT,
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
                NEIGHBORHOOD_MODE_DEFAULT,
                deathMode.getValue(),
                growthPerFood.getValue(),
                basePointsPerFood.getValue(),
                segmentLengthMultiplier.getValue()
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
