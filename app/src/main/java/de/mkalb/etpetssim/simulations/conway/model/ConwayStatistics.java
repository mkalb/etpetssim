package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsChartGroup;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Statistics of Conway's Game of Life with the alive, dead, and changed cell counts.
 */
public final class ConwayStatistics extends BaseTimedSimulationStatistics {

    public static final String KEY_ALIVE_CELLS = "aliveCells";
    public static final String KEY_DEAD_CELLS = "deadCells";
    public static final String KEY_CHANGED_CELLS = "changedCells";

    private static final String CONWAY_OBSERVATION_CELLS_ALIVE = "conway.observation.cells.alive";
    private static final String CONWAY_OBSERVATION_CELLS_DEAD = "conway.observation.cells.dead";
    private static final String CONWAY_OBSERVATION_CELLS_CHANGED = "conway.observation.cells.changed";
    private static final int CHART_WINDOW_SIZE = 50;

    private static final List<StatisticsMetric<ConwayStatistics>> METRICS = List.of(
            new StatisticsMetric<>(KEY_ALIVE_CELLS, CONWAY_OBSERVATION_CELLS_ALIVE,
                    ConwayStatistics::aliveCells,
                    StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY, CHART_WINDOW_SIZE),
            new StatisticsMetric<>(KEY_DEAD_CELLS, CONWAY_OBSERVATION_CELLS_DEAD,
                    ConwayStatistics::deadCells,
                    StatisticsExtremaMode.MIN_AND_MAX),
            new StatisticsMetric<>(KEY_CHANGED_CELLS, CONWAY_OBSERVATION_CELLS_CHANGED,
                    ConwayStatistics::changedCells,
                    StatisticsExtremaMode.MAX)
    );

    private int aliveCells;
    private int deadCells;
    private int changedCells;

    public ConwayStatistics(GridStructure gridStructure) {
        super(gridStructure);
        deadCells = totalCells();
    }

    public static List<StatisticsMetric<ConwayStatistics>> metrics() {
        return METRICS;
    }

    public void adjustCellCounts(int aliveCellsDelta,
                                 int changedCellsDelta) {
        aliveCells += aliveCellsDelta;
        deadCells = totalCells() - aliveCells;
        changedCells += changedCellsDelta;
    }

    public int aliveCells() {
        return aliveCells;
    }

    public int deadCells() {
        return deadCells;
    }

    public int changedCells() {
        return changedCells;
    }

    void initializeStartupCellCounts(int aliveCellsInitial) {
        aliveCells = aliveCellsInitial;
        deadCells = totalCells() - aliveCellsInitial;
    }

    void setCellCounts(int newAliveCells,
                       int newChangedCells) {
        aliveCells = newAliveCells;
        deadCells = totalCells() - newAliveCells;
        changedCells = newChangedCells;
    }

    @Override
    public String toString() {
        return "ConwayStatistics{" +
                baseToString() +
                ", aliveCells=" + aliveCells +
                ", deadCells=" + deadCells +
                ", changedCells=" + changedCells +
                '}';
    }

}
