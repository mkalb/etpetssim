package de.mkalb.etpetssim.simulations.snake.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import de.mkalb.etpetssim.simulations.snake.model.SnakeDeathMode;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class SnakeConfigViewModel
        extends AbstractConfigViewModel<SnakeConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.SQUARE,
            List.of(CellShape.SQUARE, CellShape.HEXAGON),
            GridEdgeBehavior.BLOCK_XY,
            List.of(GridEdgeBehavior.BLOCK_XY,
                    GridEdgeBehavior.WRAP_XY,
                    GridEdgeBehavior.BLOCK_X_WRAP_Y,
                    GridEdgeBehavior.WRAP_X_BLOCK_Y),
            100,
            GridSize.MIN_SIZE,
            1_024,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            50,
            GridSize.MIN_SIZE,
            1_024,
            GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE,
            8,
            2,
            64,
            CellDisplayMode.SHAPE,
            List.of(CellDisplayMode.SHAPE),
            ""
    );

    // Initialization
    private static final int VERTICAL_WALLS_INITIAL = 2;
    private static final int VERTICAL_WALLS_MIN = 0;
    private static final int VERTICAL_WALLS_MAX = 20;
    private static final int VERTICAL_WALLS_STEP = 1;
    private static final int FOOD_CELLS_INITIAL = 10;
    private static final int FOOD_CELLS_MIN = 0;
    private static final int FOOD_CELLS_MAX = 100;
    private static final int FOOD_CELLS_STEP = 1;
    private static final int SNAKES_INITIAL = 1;
    private static final int SNAKES_MIN = 1;
    private static final int SNAKES_MAX = 100;
    private static final int SNAKES_STEP = 1;
    private static final int INITIAL_PENDING_GROWTH_INITIAL = 2;
    private static final int INITIAL_PENDING_GROWTH_MIN = 0;
    private static final int INITIAL_PENDING_GROWTH_MAX = 10;
    private static final int INITIAL_PENDING_GROWTH_STEP = 1;

    // Rules
    private static final int GROWTH_PER_FOOD_INITIAL = 1;
    private static final int GROWTH_PER_FOOD_MIN = 1;
    private static final int GROWTH_PER_FOOD_MAX = 10;
    private static final int GROWTH_PER_FOOD_STEP = 1;
    private static final SnakeDeathMode SNAKE_DEATH_MODE_INITIAL = SnakeDeathMode.RESPAWN;
    private static final NeighborhoodMode NEIGHBORHOOD_MODE = NeighborhoodMode.EDGES_ONLY;
    // Initialization
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
    // Rules
    private final InputIntegerProperty growthPerFood = InputIntegerProperty.of(
            GROWTH_PER_FOOD_INITIAL,
            GROWTH_PER_FOOD_MIN,
            GROWTH_PER_FOOD_MAX,
            GROWTH_PER_FOOD_STEP);
    private final InputEnumProperty<SnakeDeathMode> deathMode = InputEnumProperty.of(
            SNAKE_DEATH_MODE_INITIAL,
            SnakeDeathMode.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));

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

}
