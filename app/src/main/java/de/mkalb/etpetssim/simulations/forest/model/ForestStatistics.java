package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsChartGroup;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class ForestStatistics
        extends BaseTimedSimulationStatistics {

    public static final String KEY_EMPTY_CELLS = "emptyCells";
    public static final String KEY_TREE_CELLS = "treeCells";
    public static final String KEY_BURNING_CELLS = "burningCells";

    private static final String FOREST_OBSERVATION_CELLS_EMPTY = "forest.observation.cells.empty";
    private static final String FOREST_OBSERVATION_CELLS_TREE = "forest.observation.cells.tree";
    private static final String FOREST_OBSERVATION_CELLS_BURNING = "forest.observation.cells.burning";

    private int emptyCells;
    private int treeCells;
    private int burningCells;

    public ForestStatistics(GridStructure gridStructure) {
        super(gridStructure);
        emptyCells = totalCells();
        treeCells = 0;
        burningCells = 0;
    }

    public static List<StatisticsMetric<ForestStatistics>> metrics() {
        return List.of(
                new StatisticsMetric<>(KEY_EMPTY_CELLS, FOREST_OBSERVATION_CELLS_EMPTY,
                        ForestStatistics::emptyCells,
                        StatisticsExtremaMode.MAX),
                new StatisticsMetric<>(KEY_TREE_CELLS, FOREST_OBSERVATION_CELLS_TREE,
                        ForestStatistics::treeCells,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY),
                new StatisticsMetric<>(KEY_BURNING_CELLS, FOREST_OBSERVATION_CELLS_BURNING,
                        ForestStatistics::burningCells,
                        StatisticsExtremaMode.MAX, StatisticsChartGroup.SECONDARY)
        );
    }

    public int emptyCells() {
        return emptyCells;
    }

    public int treeCells() {
        return treeCells;
    }

    public int burningCells() {
        return burningCells;
    }

    void initializeStartupCellCounts(int treeCellsInitial) {
        emptyCells = totalCells() - treeCellsInitial;
        treeCells = treeCellsInitial;
    }

    void setCellCounts(int newTreeCells, int newBurningCells) {
        emptyCells = totalCells() - newTreeCells - newBurningCells;
        treeCells = newTreeCells;
        burningCells = newBurningCells;
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
