package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

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

    public static List<StatisticsMetric<LangtonStatistics>> metrics() {
        return List.of(
                new StatisticsMetric<>(KEY_ANT_CELLS, LANGTON_OBSERVATION_ANT_CELLS,
                        LangtonStatistics::antCells,
                        StatisticsExtremaMode.NONE),
                new StatisticsMetric<>(KEY_VISITED_CELLS, LANGTON_OBSERVATION_VISITED_CELLS,
                        LangtonStatistics::visitedCells,
                        StatisticsExtremaMode.NONE)
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

    public int antCells() {
        return antCells;
    }

    public int visitedCells() {
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
