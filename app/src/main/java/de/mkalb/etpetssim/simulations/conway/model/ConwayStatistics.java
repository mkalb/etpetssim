package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.model.TimedSimulationStatistics;

/**
 * Holds statistics for a running Conway's Game of Life simulation.
 */
public final class ConwayStatistics
        implements TimedSimulationStatistics {

    private final int totalCells;

    private int stepCount;
    private long aliveCells;
    private long deadCells;
    private long maxAliveCells;
    private long timeOutMillis;
    private StepTimingStatistics stepTimingStatistics;

    public ConwayStatistics(int totalCells) {
        this.totalCells = totalCells;
        stepCount = 0;
        aliveCells = 0;
        deadCells = totalCells;
        maxAliveCells = 0;
        timeOutMillis = 0;
        stepTimingStatistics = StepTimingStatistics.empty();
    }

    public void update(int newStepCount, long newAliveCells, long newTimeOutMillis, StepTimingStatistics newStepTimingStatistics) {
        stepCount = newStepCount;
        aliveCells = newAliveCells;
        deadCells = totalCells - aliveCells;
        if (aliveCells > maxAliveCells) {
            maxAliveCells = aliveCells;
        }
        timeOutMillis = newTimeOutMillis;
        stepTimingStatistics = newStepTimingStatistics;
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }

    public long getAliveCells() {
        return aliveCells;
    }

    public long getDeadCells() {
        return deadCells;
    }

    @Override
    public int getTotalCells() {
        return totalCells;
    }

    public long getMaxAliveCells() {
        return maxAliveCells;
    }

    @Override
    public long timeOutMillis() {
        return timeOutMillis;
    }

    @Override
    public StepTimingStatistics stepTimingStatistics() {
        return stepTimingStatistics;
    }

    @Override
    public String toString() {
        return "ConwayStatistics{" +
                "totalCells=" + totalCells +
                ", stepCount=" + stepCount +
                ", aliveCells=" + aliveCells +
                ", deadCells=" + deadCells +
                ", maxAliveCells=" + maxAliveCells +
                ", timeOutMillis=" + timeOutMillis +
                ", stepTimingStatistics=" + stepTimingStatistics +
                '}';
    }

}