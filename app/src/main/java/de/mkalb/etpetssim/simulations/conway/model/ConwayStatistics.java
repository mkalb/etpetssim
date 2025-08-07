package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.simulations.model.SimulationStatistics;

/**
 * Holds statistics for a running Conway's Game of Life simulation.
 */
public final class ConwayStatistics
        implements SimulationStatistics {

    private final int totalCells;

    private int stepCount;
    private long aliveCells;
    private long deadCells;
    private long maxAliveCells;

    public ConwayStatistics(int totalCells) {
        this.totalCells = totalCells;
        stepCount = 0;
        aliveCells = 0;
        deadCells = totalCells;
        maxAliveCells = 0;
    }

    public void update(int newStepCount, long newAliveCells) {
        stepCount = newStepCount;
        aliveCells = newAliveCells;
        deadCells = totalCells - aliveCells;
        if (aliveCells > maxAliveCells) {
            maxAliveCells = aliveCells;
        }
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

}