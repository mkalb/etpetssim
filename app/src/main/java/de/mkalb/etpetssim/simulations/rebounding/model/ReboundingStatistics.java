package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

public final class ReboundingStatistics
        extends AbstractTimedSimulationStatistics {

    private int wallCells;
    private int movingEntityCells;

    public ReboundingStatistics(int totalCells) {
        super(totalCells);
        wallCells = 0;
        movingEntityCells = 0;
    }

    void update(int newStepCount,
                StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    void updateInitialCells(int wallCellsInitial, int movingEntityCellsInitial) {
        wallCells = wallCellsInitial;
        movingEntityCells = movingEntityCellsInitial;
    }

    public void decreaseWallCells() {
        wallCells -= 1;
    }

    public int getWallCells() {
        return wallCells;
    }

    public void decreaseMovingEntityCells() {
        movingEntityCells -= 1;
    }

    public int getMovingEntityCells() {
        return movingEntityCells;
    }

    @Override
    public String toString() {
        return "ReboundingStatistics{" +
                baseToString() +
                ", wallCells=" + wallCells +
                ", movingEntityCells=" + movingEntityCells +
                '}';
    }

}
