package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class SnakeStatistics
        extends BaseTimedSimulationStatistics {

    public static final String KEY_SNAKE_HEAD_CELLS = "snakeHeadCells";
    public static final String KEY_LIVING_SNAKE_HEAD_CELLS = "livingSnakeHeadCells";
    public static final String KEY_WALL_CELLS = "wallCells";
    public static final String KEY_FOOD_CELLS = "foodCells";
    public static final String KEY_CUMULATIVE_SNAKE_DEATH_COUNT = "cumulativeSnakeDeathCount";

    private static final String SNAKE_OBSERVATION_CELLS_SNAKE_HEAD = "snake.observation.cells.snakehead";
    private static final String SNAKE_OBSERVATION_CELLS_LIVING_SNAKE_HEAD = "snake.observation.cells.livingsnakehead";
    private static final String SNAKE_OBSERVATION_CELLS_WALL = "snake.observation.cells.wall";
    private static final String SNAKE_OBSERVATION_CELLS_FOOD = "snake.observation.cells.food";
    private static final String SNAKE_OBSERVATION_CUMULATIVE_SNAKE_DEATH_COUNT = "snake.observation.cumulativesnakedeathcount";

    private int snakeHeadCells;
    private int livingSnakeHeadCells;
    private int wallCells;
    private int foodCells;
    private int cumulativeSnakeDeathCount;

    public SnakeStatistics(GridStructure gridStructure) {
        super(gridStructure);
        snakeHeadCells = 0;
        livingSnakeHeadCells = 0;
        wallCells = 0;
        foodCells = 0;
        cumulativeSnakeDeathCount = 0;
    }

    public static List<StatisticsMetric<SnakeStatistics>> metrics() {
        return List.of(
                new StatisticsMetric<>(KEY_SNAKE_HEAD_CELLS, SNAKE_OBSERVATION_CELLS_SNAKE_HEAD,
                        SnakeStatistics::snakeHeadCells,
                        StatisticsExtremaMode.NONE),
                new StatisticsMetric<>(KEY_LIVING_SNAKE_HEAD_CELLS, SNAKE_OBSERVATION_CELLS_LIVING_SNAKE_HEAD,
                        SnakeStatistics::livingSnakeHeadCells,
                        StatisticsExtremaMode.NONE),
                new StatisticsMetric<>(KEY_WALL_CELLS, SNAKE_OBSERVATION_CELLS_WALL,
                        SnakeStatistics::wallCells,
                        StatisticsExtremaMode.NONE),
                new StatisticsMetric<>(KEY_FOOD_CELLS, SNAKE_OBSERVATION_CELLS_FOOD,
                        SnakeStatistics::foodCells,
                        StatisticsExtremaMode.NONE),
                new StatisticsMetric<>(KEY_CUMULATIVE_SNAKE_DEATH_COUNT, SNAKE_OBSERVATION_CUMULATIVE_SNAKE_DEATH_COUNT,
                        SnakeStatistics::cumulativeSnakeDeathCount,
                        StatisticsExtremaMode.NONE)
        );
    }

    public void decrementSnakeHeadCells() {
        snakeHeadCells--;
    }

    public void incrementSnakeHeadCells() {
        snakeHeadCells++;
    }

    public void decrementLivingSnakeHeadCells() {
        livingSnakeHeadCells--;
    }

    public void incrementLivingSnakeHeadCells() {
        livingSnakeHeadCells++;
    }

    public void decrementFoodCells() {
        foodCells--;
    }

    public void adjustWallCells(int wallCellsDelta) {
        wallCells += wallCellsDelta;
    }

    public void adjustFoodCells(int foodCellsDelta) {
        foodCells += foodCellsDelta;
    }

    public int snakeHeadCells() {
        return snakeHeadCells;
    }

    public int livingSnakeHeadCells() {
        return livingSnakeHeadCells;
    }

    public int foodCells() {
        return foodCells;
    }

    public int wallCells() {
        return wallCells;
    }

    public int cumulativeSnakeDeathCount() {
        return cumulativeSnakeDeathCount;
    }

    void initializeStartupCellCounts(int snakeHeadCellsInitial,
                                     int wallCellsInitial,
                                     int foodCellsInitial) {
        snakeHeadCells = snakeHeadCellsInitial;
        livingSnakeHeadCells = snakeHeadCellsInitial;
        wallCells = wallCellsInitial;
        foodCells = foodCellsInitial;
    }

    void incrementCumulativeSnakeDeathCount() {
        cumulativeSnakeDeathCount++;
    }

    @Override
    public String toString() {
        return "SnakeStatistics{" +
                baseToString() +
                ", snakeHeadCells=" + snakeHeadCells +
                ", livingSnakeHeadCells=" + livingSnakeHeadCells +
                ", wallCells=" + wallCells +
                ", foodCells=" + foodCells +
                ", cumulativeSnakeDeathCount=" + cumulativeSnakeDeathCount +
                '}';
    }

}
