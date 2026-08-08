package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class ForestStatistics
        extends BaseTimedSimulationStatistics {

    private static final String FOREST_OBSERVATION_EMPTY_CELLS = "forest.observation.cells.empty";
    private static final String FOREST_OBSERVATION_TREE_CELLS = "forest.observation.cells.tree";
    private static final String FOREST_OBSERVATION_BURNING_CELLS = "forest.observation.cells.burning";

    private int emptyCells;
    private int treeCells;
    private int burningCells;

    public ForestStatistics(GridStructure gridStructure) {
        super(gridStructure);
        emptyCells = getTotalCells();
        treeCells = 0;
        burningCells = 0;
    }

    public static List<StatisticMetric<ForestStatistics>> metrics() {
        return List.of(
                new StatisticMetric<>("emptyCells", FOREST_OBSERVATION_EMPTY_CELLS, ForestStatistics::getEmptyCells,
                        StatisticExtremaMode.NONE),
                new StatisticMetric<>("treeCells", FOREST_OBSERVATION_TREE_CELLS, ForestStatistics::getTreeCells,
                        StatisticExtremaMode.MAX),
                new StatisticMetric<>("burningCells", FOREST_OBSERVATION_BURNING_CELLS, ForestStatistics::getBurningCells,
                        StatisticExtremaMode.MAX)
        );
    }

    void initializeStartupCellCounts(int treeCellsInitial) {
        emptyCells = getTotalCells() - treeCellsInitial;
        treeCells = treeCellsInitial;
    }

    void updateCellCounts(int newTreeCells, int newBurningCells) {
        emptyCells = getTotalCells() - newTreeCells - newBurningCells;
        treeCells = newTreeCells;
        burningCells = newBurningCells;
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
                ", emptyCells=" + emptyCells +
                ", treeCells=" + treeCells +
                ", burningCells=" + burningCells +
                '}';
    }

}
