package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

public final class SnakeStatistics
        extends AbstractTimedSimulationStatistics {

    private int snakeHeadCells;
    private int foodCells;
    private int deaths;

    public SnakeStatistics(int totalCells) {
        super(totalCells);
        snakeHeadCells = 0;
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
        foodCells = foodCellsInitial;
    }

    public void decreaseSnakeHeadCells() {
        snakeHeadCells--;
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
                ", foodCells=" + foodCells +
                ", deaths=" + deaths +
                '}';
    }

}
