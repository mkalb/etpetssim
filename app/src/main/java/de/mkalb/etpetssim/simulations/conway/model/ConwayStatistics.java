package de.mkalb.etpetssim.simulations.conway.model;

/**
 * Holds statistics for a running Conway's Game of Life simulation.
 */
public final class ConwayStatistics {

    private final long totalCells;

    private int stepCount;
    private long aliveCells;
    private long deadCells;
    private long maxAliveCells;

    public ConwayStatistics(long totalCells) {
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

    public int getStepCount() {
        return stepCount;
    }

    public long getAliveCells() {
        return aliveCells;
    }

    public long getDeadCells() {
        return deadCells;
    }

    public long getTotalCells() {
        return totalCells;
    }

    public long getMaxAliveCells() {
        return maxAliveCells;
    }

}