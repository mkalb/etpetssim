package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsChartGroup;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Statistics of the Forest-fire model with the empty, tree, and burning cell counts and the cumulative lightning
 * strikes and burned trees.
 */
public final class ForestStatistics extends BaseSimulationStatistics {

    public static final String KEY_EMPTY_CELLS = "emptyCells";
    public static final String KEY_TREE_CELLS = "treeCells";
    public static final String KEY_BURNING_CELLS = "burningCells";
    public static final String KEY_CUMULATIVE_LIGHTNING_STRIKE_COUNT = "cumulativeLightningStrikeCount";
    public static final String KEY_CUMULATIVE_BURNED_TREE_COUNT = "cumulativeBurnedTreeCount";

    private static final String FOREST_OBSERVATION_CELLS_EMPTY = "forest.observation.cells.empty";
    private static final String FOREST_OBSERVATION_CELLS_TREE = "forest.observation.cells.tree";
    private static final String FOREST_OBSERVATION_CELLS_BURNING = "forest.observation.cells.burning";
    private static final String FOREST_OBSERVATION_CUMULATIVE_LIGHTNING_STRIKE_COUNT =
            "forest.observation.cumulativelightningstrikecount";
    private static final String FOREST_OBSERVATION_CUMULATIVE_BURNED_TREE_COUNT =
            "forest.observation.cumulativeburnedtreecount";

    private static final List<StatisticsMetric<ForestStatistics>> METRICS = List.of(
            new StatisticsMetric<>(KEY_EMPTY_CELLS, FOREST_OBSERVATION_CELLS_EMPTY,
                    ForestStatistics::emptyCells,
                    StatisticsExtremaMode.MAX),
            new StatisticsMetric<>(KEY_TREE_CELLS, FOREST_OBSERVATION_CELLS_TREE,
                    ForestStatistics::treeCells,
                    StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY),
            new StatisticsMetric<>(KEY_BURNING_CELLS, FOREST_OBSERVATION_CELLS_BURNING,
                    ForestStatistics::burningCells,
                    StatisticsExtremaMode.MAX, StatisticsChartGroup.SECONDARY),
            new StatisticsMetric<>(KEY_CUMULATIVE_LIGHTNING_STRIKE_COUNT,
                    FOREST_OBSERVATION_CUMULATIVE_LIGHTNING_STRIKE_COUNT,
                    ForestStatistics::cumulativeLightningStrikeCount,
                    StatisticsExtremaMode.NONE),
            new StatisticsMetric<>(KEY_CUMULATIVE_BURNED_TREE_COUNT,
                    FOREST_OBSERVATION_CUMULATIVE_BURNED_TREE_COUNT,
                    ForestStatistics::cumulativeBurnedTreeCount,
                    StatisticsExtremaMode.NONE)
    );

    private int emptyCells;
    private int treeCells;
    private int burningCells;
    private int cumulativeLightningStrikeCount;
    private int cumulativeBurnedTreeCount;

    public ForestStatistics(GridStructure gridStructure) {
        super(gridStructure);
        emptyCells = totalCells();
    }

    public static List<StatisticsMetric<ForestStatistics>> metrics() {
        return METRICS;
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

    public int cumulativeLightningStrikeCount() {
        return cumulativeLightningStrikeCount;
    }

    public int cumulativeBurnedTreeCount() {
        return cumulativeBurnedTreeCount;
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

    void updateStepCounts(int newTreeCells, int newBurningCells, int lightningStrikes, int burnedTrees) {
        setCellCounts(newTreeCells, newBurningCells);
        cumulativeLightningStrikeCount += lightningStrikes;
        cumulativeBurnedTreeCount += burnedTrees;
    }

    @Override
    public String toString() {
        return "ForestStatistics{" +
                baseToString() +
                ", emptyCells=" + emptyCells +
                ", treeCells=" + treeCells +
                ", burningCells=" + burningCells +
                ", cumulativeLightningStrikeCount=" + cumulativeLightningStrikeCount +
                ", cumulativeBurnedTreeCount=" + cumulativeBurnedTreeCount +
                '}';
    }

}
