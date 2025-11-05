package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

/**
 * Holds statistics for a running Forest-fire model simulation.
 */
public final class ForestStatistics
        extends AbstractTimedSimulationStatistics {

    private int emptyCells;
    private int treeCells;
    private int burningCells;
    private int maxTreeCells;
    private int maxBurningCells;

    public ForestStatistics(int totalCells) {
        super(totalCells);
        emptyCells = totalCells;
        treeCells = 0;
        burningCells = 0;
        maxTreeCells = 0;
        maxBurningCells = 0;
    }

    void update(int newStepCount,
                StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    void updateCells(int newTreeCells, int newBurningCells) {
        emptyCells = getTotalCells() - newTreeCells - newBurningCells;
        treeCells = newTreeCells;
        burningCells = newBurningCells;
        maxTreeCells = Math.max(newTreeCells, maxTreeCells);
        maxBurningCells = Math.max(newBurningCells, maxBurningCells);
    }

    public int getEmptyCells() {
        return emptyCells;
    }

    public int getTreeCells() {
        return treeCells;
    }

    public int getBurningCells() {
        return burningCells;
    }

    public int getMaxTreeCells() {
        return maxTreeCells;
    }

    public int getMaxBurningCells() {
        return maxBurningCells;
    }

    @Override
    public String toString() {
        return "ForestStatistics{" +
                baseToString() +
                ", emptyCells=" + emptyCells +
                ", treeCells=" + treeCells +
                ", burningCells=" + burningCells +
                ", maxTreeCells=" + maxTreeCells +
                ", maxBurningCells=" + maxBurningCells +
                '}';
    }

}