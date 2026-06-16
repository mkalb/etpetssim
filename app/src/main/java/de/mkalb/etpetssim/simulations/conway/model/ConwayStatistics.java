package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class ConwayStatistics
        extends BaseTimedSimulationStatistics {

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

    void initializeStartupCellCounts(int aliveCellsInitial) {
        maxAliveCells = aliveCellsInitial;
        aliveCells = aliveCellsInitial;
        deadCells = getTotalCells() - aliveCellsInitial;
    }

    void updateCellCounts(int newAliveCells,
                          int newChangedCells) {
        maxAliveCells = Math.max(newAliveCells, maxAliveCells);
        aliveCells = newAliveCells;
        deadCells = getTotalCells() - newAliveCells;
        changedCells = newChangedCells;
    }

    public void adjustCellCounts(int aliveCellsDelta,
                                 int changedCellsDelta) {
        int newAliveCells = aliveCells + aliveCellsDelta;
        maxAliveCells = Math.max(newAliveCells, maxAliveCells);
        aliveCells = newAliveCells;
        deadCells = getTotalCells() - newAliveCells;
        changedCells += changedCellsDelta;
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
