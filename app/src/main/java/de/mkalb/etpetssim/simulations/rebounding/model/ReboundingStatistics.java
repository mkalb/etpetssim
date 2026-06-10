package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class ReboundingStatistics
        extends BaseTimedSimulationStatistics {

    private int wallCells;
    private int movingEntityCells;

    public ReboundingStatistics(GridStructure gridStructure) {
        super(gridStructure);
        wallCells = 0;
        movingEntityCells = 0;
    }

    void updateInitialCells(int wallCellsInitial, int movingEntityCellsInitial) {
        wallCells = wallCellsInitial;
        movingEntityCells = movingEntityCellsInitial;
    }

    public void increaseWallCells() {
        wallCells += 1;
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
