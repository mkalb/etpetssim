package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

/**
 * Holds statistics for a running Conway's Game of Life simulation.
 */
public final class ConwayStatistics
        extends AbstractTimedSimulationStatistics {

    private int maxAliveCells;
    private int aliveCells;
    private int deadCells;
    private int changedCells;

    public ConwayStatistics(GridStructure gridStructure) {
        super(gridStructure);
        maxAliveCells = 0;
        aliveCells = 0;
        deadCells = getTotalCells();
        changedCells = 0;
    }

    void update(int newStepCount,
                StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    void updateCells(int newAliveCells, int newChangedCells) {
        maxAliveCells = Math.max(newAliveCells, maxAliveCells);
        aliveCells = newAliveCells;
        deadCells = getTotalCells() - newAliveCells;
        changedCells = newChangedCells;
    }

    public int getMaxAliveCells() {
        return maxAliveCells;
    }

    public int getAliveCells() {
        return aliveCells;
    }

    public int getDeadCells() {
        return deadCells;
    }

    public int getChangedCells() {
        return changedCells;
    }

    @Override
    public String toString() {
        return "ConwayStatistics{" +
                baseToString() +
                ", maxAliveCells=" + maxAliveCells +
                ", aliveCells=" + aliveCells +
                ", deadCells=" + deadCells +
                ", changedCells=" + changedCells +
                '}';
    }

}