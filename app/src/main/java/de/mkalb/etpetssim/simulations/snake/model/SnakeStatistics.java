package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

public final class SnakeStatistics
        extends AbstractTimedSimulationStatistics {

    private int snakeHeadCells;
    private int livingSnakeHeadCells;
    private int foodCells;
    private int deaths;

    public SnakeStatistics(int totalCells) {
        super(totalCells);
        snakeHeadCells = 0;
        livingSnakeHeadCells = 0;
        foodCells = 0;
        deaths = 0;
    }

    public void update(int newStepCount,
                       StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    void updateInitialCells(int snakeHeadCellsInitial,
                            int foodCellsInitial) {
        snakeHeadCells = snakeHeadCellsInitial;
        livingSnakeHeadCells = snakeHeadCellsInitial;
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

    void incrementDeaths() {
        deaths++;
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

    public int getDeaths() {
        return deaths;
    }

    @Override
    public String toString() {
        return "SnakeStatistics{" +
                baseToString() +
                ", snakeHeadCells=" + snakeHeadCells +
                ", livingSnakeHeadCells=" + livingSnakeHeadCells +
                ", foodCells=" + foodCells +
                ", deaths=" + deaths +
                '}';
    }

}
