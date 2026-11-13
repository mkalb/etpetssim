package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Statistics of Langton's Ant with the ant and visited cell counts.
 */
public final class LangtonStatistics extends BaseSimulationStatistics {

    public static final String KEY_ANT_CELLS = "antCells";
    public static final String KEY_VISITED_CELLS = "visitedCells";

    private static final String LANGTON_OBSERVATION_CELLS_ANT = "langton.observation.cells.ant";
    private static final String LANGTON_OBSERVATION_CELLS_VISITED = "langton.observation.cells.visited";

    private static final List<StatisticsMetric<LangtonStatistics>> METRICS = List.of(
            new StatisticsMetric<>(KEY_ANT_CELLS, LANGTON_OBSERVATION_CELLS_ANT,
                    LangtonStatistics::antCells,
                    StatisticsExtremaMode.NONE),
            new StatisticsMetric<>(KEY_VISITED_CELLS, LANGTON_OBSERVATION_CELLS_VISITED,
                    LangtonStatistics::visitedCells,
                    StatisticsExtremaMode.NONE)
    );

    private int antCells;
    private int visitedCells;

    public LangtonStatistics(GridStructure gridStructure) {
        super(gridStructure);
    }

    public static List<StatisticsMetric<LangtonStatistics>> metrics() {
        return METRICS;
    }

    public int antCells() {
        return antCells;
    }

    public int visitedCells() {
        return visitedCells;
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

    @Override
    public String toString() {
        return "LangtonStatistics{" +
                baseToString() +
                ", antCells=" + antCells +
                ", visitedCells=" + visitedCells +
                '}';
    }

}
