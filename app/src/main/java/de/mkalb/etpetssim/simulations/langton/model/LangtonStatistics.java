package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class LangtonStatistics
        extends BaseTimedSimulationStatistics {

    private int antCells;
    private int visitedCells;

    public LangtonStatistics(GridStructure gridStructure) {
        super(gridStructure);
        antCells = 0;
        visitedCells = 0;
    }

    void initializeStartupCellCounts(int antCellsInitial) {
        antCells = antCellsInitial;
        visitedCells = antCellsInitial;
    }

    void adjustCellCounts(int antCellsDelta,
                          int visitedCellsDelta) {
        antCells += antCellsDelta;
        visitedCells += visitedCellsDelta;
    }

    public int getAntCells() {
        return antCells;
    }

    public int getVisitedCells() {
        return visitedCells;
    }

    @Override
    public String toString() {
        return "LangtonStatistics{" +
                baseToString() +
                ", antCells=" + antCells +
                ", visitedCells=" + visitedCells +
                '}';
    }

}
