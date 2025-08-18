package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationStatistics;

/**
 * Holds statistics for a running Conway's Game of Life simulation.
 */
public final class ConwayStatistics
        extends AbstractTimedSimulationStatistics {

    private long aliveCells;
    private long deadCells;
    private long maxAliveCells;

    public ConwayStatistics(int totalCells) {
        super(totalCells);
        aliveCells = 0;
        deadCells = totalCells;
        maxAliveCells = 0;
    }

    public void update(int newStepCount,
                       long newAliveCells,
                       StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
        aliveCells = newAliveCells;
        deadCells = getTotalCells() - aliveCells;
        if (aliveCells > maxAliveCells) {
            maxAliveCells = aliveCells;
        }
    }

    public long getAliveCells() {
        return aliveCells;
    }

    public long getDeadCells() {
        return deadCells;
    }

    public long getMaxAliveCells() {
        return maxAliveCells;
    }

    @Override
    public String toString() {
        return "ConwayStatistics{" +
                baseToString() +
                ", aliveCells=" + aliveCells +
                ", deadCells=" + deadCells +
                ", maxAliveCells=" + maxAliveCells +
                '}';
    }

}