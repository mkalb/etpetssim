package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

public final class LangtonStatistics
        extends AbstractTimedSimulationStatistics {

    private int antCells;
    private int visitedCells;

    public LangtonStatistics(int totalCells) {
        super(totalCells);
        antCells = 0;
        visitedCells = 0;
    }

    void update(int newStepCount,
                StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    void updateCells(int antCellsChange, int visitedCellChange) {
        antCells += antCellsChange;
        visitedCells += visitedCellChange;
    }

    public int getAntCells() {
        return antCells;
    }

    public int getVisitedCells() {
        return visitedCells;
    }

    @Override
    public String toString() {
        return "ConwayStatistics{" +
                baseToString() +
                ", antCells=" + antCells +
                ", visitedCells=" + visitedCells +
                '}';
    }

}