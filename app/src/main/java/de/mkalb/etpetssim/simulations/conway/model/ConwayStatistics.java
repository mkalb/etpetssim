package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationStatistics;

/**
 * Holds statistics for a running Conway's Game of Life simulation.
 */
public final class ConwayStatistics
        extends AbstractTimedSimulationStatistics {

    private int aliveCells;
    private int deadCells;
    private int maxAliveCells;
    private int changedCells;

    public ConwayStatistics(int totalCells) {
        super(totalCells);
        aliveCells = 0;
        deadCells = totalCells;
        maxAliveCells = 0;
        changedCells = 0;
    }

    void update(int newStepCount,
                StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    void updateCells(int newAliveCells, int newChangedCells) {
        aliveCells = newAliveCells;
        deadCells = getTotalCells() - aliveCells;
        if (aliveCells > maxAliveCells) {
            maxAliveCells = aliveCells;
        }
        changedCells = newChangedCells;
    }

    public int getAliveCells() {
        return aliveCells;
    }

    public int getDeadCells() {
        return deadCells;
    }

    public int getMaxAliveCells() {
        return maxAliveCells;
    }

    public int getChangedCells() {
        return changedCells;
    }

    @Override
    public String toString() {
        return "ConwayStatistics{" +
                baseToString() +
                ", aliveCells=" + aliveCells +
                ", deadCells=" + deadCells +
                ", maxAliveCells=" + maxAliveCells +
                ", changedCells=" + changedCells +
                '}';
    }

}