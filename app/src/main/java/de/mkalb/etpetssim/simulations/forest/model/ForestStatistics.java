package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class ForestStatistics
        extends BaseTimedSimulationStatistics {

    private int maxTreeCells;
    private int maxBurningCells;

    private int emptyCells;
    private int treeCells;
    private int burningCells;

    public ForestStatistics(GridStructure gridStructure) {
        super(gridStructure);
        emptyCells = getTotalCells();
        treeCells = 0;
        burningCells = 0;
        maxTreeCells = 0;
        maxBurningCells = 0;
    }

    void initializeStartupCellCounts(int treeCellsInitial) {
        maxTreeCells = treeCellsInitial;
        emptyCells = getTotalCells() - treeCellsInitial;
        treeCells = treeCellsInitial;
    }

    void updateCellCounts(int newTreeCells, int newBurningCells) {
        emptyCells = getTotalCells() - newTreeCells - newBurningCells;
        treeCells = newTreeCells;
        burningCells = newBurningCells;
        maxTreeCells = Math.max(newTreeCells, maxTreeCells);
        maxBurningCells = Math.max(newBurningCells, maxBurningCells);
    }

    public int getMaxTreeCells() {
        return maxTreeCells;
    }

    public int getMaxBurningCells() {
        return maxBurningCells;
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

    @Override
    public String toString() {
        return "ForestStatistics{" +
                baseToString() +
                ", maxTreeCells=" + maxTreeCells +
                ", maxBurningCells=" + maxBurningCells +
                ", emptyCells=" + emptyCells +
                ", treeCells=" + treeCells +
                ", burningCells=" + burningCells +
                '}';
    }

}
