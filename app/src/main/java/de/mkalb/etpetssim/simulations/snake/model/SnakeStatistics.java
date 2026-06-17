package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class SnakeStatistics
        extends BaseTimedSimulationStatistics {

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

    void initializeStartupCellCounts(int snakeHeadCellsInitial,
                                     int wallCellsInitial,
                                     int foodCellsInitial) {
        snakeHeadCells = snakeHeadCellsInitial;
        livingSnakeHeadCells = snakeHeadCellsInitial;
        wallCells = wallCellsInitial;
        foodCells = foodCellsInitial;
    }

    public void decreaseSnakeHeadCells() {
        snakeHeadCells--;
    }

    public void decreaseLivingSnakeHeadCells() {
        livingSnakeHeadCells--;
    }

    public void increaseLivingSnakeHeadCells() {
        livingSnakeHeadCells++;
    }

    public void decreaseFoodCells() {
        foodCells--;
    }

    public void adjustWallCells(int wallCellsDelta) {
        wallCells += wallCellsDelta;
    }

    public void adjustFoodCells(int foodCellsDelta) {
        foodCells += foodCellsDelta;
    }

    void incrementCumulativeSnakeDeathCount() {
        cumulativeSnakeDeathCount++;
    }

    public int getSnakeHeadCells() {
        return snakeHeadCells;
    }

    public int getLivingSnakeHeadCells() {
        return livingSnakeHeadCells;
    }

    public int getFoodCells() {
        return foodCells;
    }

    public int getWallCells() {
        return wallCells;
    }

    public int getCumulativeSnakeDeathCount() {
        return cumulativeSnakeDeathCount;
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
