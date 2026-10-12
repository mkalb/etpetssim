package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class ConwayStatistics
        extends BaseTimedSimulationStatistics {

    public static final String KEY_ALIVE_CELLS = "aliveCells";
    public static final String KEY_DEAD_CELLS = "deadCells";
    public static final String KEY_CHANGED_CELLS = "changedCells";

    private static final String CONWAY_OBSERVATION_ALIVE_CELLS = "conway.observation.cells.alive";
    private static final String CONWAY_OBSERVATION_DEAD_CELLS = "conway.observation.cells.dead";
    private static final String CONWAY_OBSERVATION_CHANGED_CELLS = "conway.observation.cells.changed";
    private static final int CHART_WINDOW_SIZE = 50;

    private int aliveCells;
    private int deadCells;
    private int changedCells;

    public ConwayStatistics(GridStructure gridStructure) {
        super(gridStructure);
        aliveCells = 0;
        deadCells = totalCells();
        changedCells = 0;
    }

    public static List<StatisticsMetric<ConwayStatistics>> metrics() {
        return List.of(
                new StatisticsMetric<>(KEY_ALIVE_CELLS, CONWAY_OBSERVATION_ALIVE_CELLS,
                        ConwayStatistics::aliveCells,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY, CHART_WINDOW_SIZE),
                new StatisticsMetric<>(KEY_DEAD_CELLS, CONWAY_OBSERVATION_DEAD_CELLS,
                        ConwayStatistics::deadCells,
                        StatisticsExtremaMode.MIN_AND_MAX),
                new StatisticsMetric<>(KEY_CHANGED_CELLS, CONWAY_OBSERVATION_CHANGED_CELLS,
                        ConwayStatistics::changedCells,
                        StatisticsExtremaMode.MAX)
        );
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

    public void adjustCellCounts(int aliveCellsDelta,
                                 int changedCellsDelta) {
        int newAliveCells = aliveCells + aliveCellsDelta;
        aliveCells = newAliveCells;
        deadCells = totalCells() - newAliveCells;
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
