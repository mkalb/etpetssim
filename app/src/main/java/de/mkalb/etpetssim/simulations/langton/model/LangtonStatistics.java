package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class LangtonStatistics
        extends BaseTimedSimulationStatistics {

    public static final String KEY_ANT_CELLS = "antCells";
    public static final String KEY_VISITED_CELLS = "visitedCells";

    private static final String LANGTON_OBSERVATION_ANT_CELLS = "langton.observation.cells.ant";
    private static final String LANGTON_OBSERVATION_VISITED_CELLS = "langton.observation.cells.visited";

    private int antCells;
    private int visitedCells;

    public LangtonStatistics(GridStructure gridStructure) {
        super(gridStructure);
        antCells = 0;
        visitedCells = 0;
    }

    public static List<StatisticMetric<LangtonStatistics>> metrics() {
        return List.of(
                new StatisticMetric<>(KEY_ANT_CELLS, LANGTON_OBSERVATION_ANT_CELLS, LangtonStatistics::getAntCells,
                        StatisticExtremaMode.NONE),
                new StatisticMetric<>(KEY_VISITED_CELLS, LANGTON_OBSERVATION_VISITED_CELLS, LangtonStatistics::getVisitedCells,
                        StatisticExtremaMode.MAX)
        );
    }

    void initializeStartupCellCounts(int antCellsInitial) {
        antCells = antCellsInitial;
        visitedCells = antCellsInitial;
    }

    void adjustCellCounts(int antCellsDelta,
                          int visitedCellsDelta) {
        antCells += antCellsDelta;
        visitedCells += visitedCellsDelta;
    }

    public int getAntCells() {
        return antCells;
    }

    public int getVisitedCells() {
        return visitedCells;
    }

    @Override
    public String toString() {
        return "LangtonStatistics{" +
                baseToString() +
                ", antCells=" + antCells +
                ", visitedCells=" + visitedCells +
                '}';
    }

}
