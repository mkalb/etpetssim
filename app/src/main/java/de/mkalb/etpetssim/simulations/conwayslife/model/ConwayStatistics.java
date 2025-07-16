package de.mkalb.etpetssim.simulations.conwayslife.model;

/**
 * Holds statistics for a running Conway's Game of Life simulation.
 */
public final class ConwayStatistics {

    private final long totalCells;

    private long step;
    private long aliveCells;
    private long deadCells;
    private long maxAliveCells;

    public ConwayStatistics(long totalCells) {
        this.totalCells = totalCells;
        step = 0;
        aliveCells = 0;
        deadCells = totalCells;
        maxAliveCells = 0;
    }

    public void update(long newStep, long newAliveCells) {
        step = newStep;
        aliveCells = newAliveCells;
        deadCells = totalCells - aliveCells;
        if (aliveCells > maxAliveCells) {
            maxAliveCells = aliveCells;
        }
    }

    public long getStep() {
        return step;
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