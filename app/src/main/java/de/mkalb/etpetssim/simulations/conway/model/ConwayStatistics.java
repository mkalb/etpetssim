package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class ConwayStatistics
        extends BaseTimedSimulationStatistics {

    private static final String CONWAY_OBSERVATION_ALIVE_CELLS = "conway.observation.cells.alive";
    private static final String CONWAY_OBSERVATION_DEAD_CELLS = "conway.observation.cells.dead";
    private static final String CONWAY_OBSERVATION_CHANGED_CELLS = "conway.observation.cells.changed";

    private int aliveCells;
    private int deadCells;
    private int changedCells;

    public ConwayStatistics(GridStructure gridStructure) {
        super(gridStructure);
        aliveCells = 0;
        deadCells = getTotalCells();
        changedCells = 0;
    }

    public static List<StatisticMetric<ConwayStatistics>> metrics() {
        return List.of(
                new StatisticMetric<>("aliveCells", CONWAY_OBSERVATION_ALIVE_CELLS, ConwayStatistics::getAliveCells,
                        StatisticExtremaMode.MAX),
                new StatisticMetric<>("deadCells", CONWAY_OBSERVATION_DEAD_CELLS, ConwayStatistics::getDeadCells,
                        StatisticExtremaMode.NONE),
                new StatisticMetric<>("changedCells", CONWAY_OBSERVATION_CHANGED_CELLS, ConwayStatistics::getChangedCells,
                        StatisticExtremaMode.NONE)
        );
    }

    void initializeStartupCellCounts(int aliveCellsInitial) {
        aliveCells = aliveCellsInitial;
        deadCells = getTotalCells() - aliveCellsInitial;
    }

    void updateCellCounts(int newAliveCells,
                          int newChangedCells) {
        aliveCells = newAliveCells;
        deadCells = getTotalCells() - newAliveCells;
        changedCells = newChangedCells;
    }

    public void adjustCellCounts(int aliveCellsDelta,
                                 int changedCellsDelta) {
        int newAliveCells = aliveCells + aliveCellsDelta;
        aliveCells = newAliveCells;
        deadCells = getTotalCells() - newAliveCells;
        changedCells += changedCellsDelta;
    }

    public int getAliveCells() {
        return aliveCells;
    }

    public int getDeadCells() {
        return deadCells;
    }

    public int getChangedCells() {
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
