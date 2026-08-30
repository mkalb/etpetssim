package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class ForestStatistics
        extends BaseTimedSimulationStatistics {

    public static final String KEY_EMPTY_CELLS = "emptyCells";
    public static final String KEY_TREE_CELLS = "treeCells";
    public static final String KEY_BURNING_CELLS = "burningCells";

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
                new StatisticMetric<>(KEY_EMPTY_CELLS, FOREST_OBSERVATION_EMPTY_CELLS,
                        ForestStatistics::getEmptyCells,
                        StatisticExtremaMode.MAX),
                new StatisticMetric<>(KEY_TREE_CELLS, FOREST_OBSERVATION_TREE_CELLS,
                        ForestStatistics::getTreeCells,
                        StatisticExtremaMode.MIN_AND_MAX, StatisticChartGroup.PRIMARY),
                new StatisticMetric<>(KEY_BURNING_CELLS, FOREST_OBSERVATION_BURNING_CELLS,
                        ForestStatistics::getBurningCells,
                        StatisticExtremaMode.MAX, StatisticChartGroup.SECONDARY)
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
