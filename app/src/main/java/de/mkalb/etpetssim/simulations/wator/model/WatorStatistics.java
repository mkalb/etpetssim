package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class WatorStatistics
        extends BaseTimedSimulationStatistics {

    public static final String KEY_FISH_CELLS = "fishCells";
    public static final String KEY_SHARK_CELLS = "sharkCells";

    private static final String WATOR_OBSERVATION_FISH_CELLS = "wator.observation.cells.fish";
    private static final String WATOR_OBSERVATION_SHARK_CELLS = "wator.observation.cells.shark";
    private static final int CHART_WINDOW_SIZE = 500;

    private int fishCells;
    private int sharkCells;

    public WatorStatistics(GridStructure gridStructure) {
        super(gridStructure);
        fishCells = 0;
        sharkCells = 0;
    }

    public static List<StatisticsMetric<WatorStatistics>> metrics() {
        return List.of(
                new StatisticsMetric<>(KEY_FISH_CELLS, WATOR_OBSERVATION_FISH_CELLS,
                        WatorStatistics::fishCells,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY, CHART_WINDOW_SIZE),
                new StatisticsMetric<>(KEY_SHARK_CELLS, WATOR_OBSERVATION_SHARK_CELLS,
                        WatorStatistics::sharkCells,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY, CHART_WINDOW_SIZE)
        );
    }

    void initializeStartupCellCounts(int fishCellsInitial,
                                     int sharkCellsInitial) {
        fishCells = fishCellsInitial;
        sharkCells = sharkCellsInitial;
    }

    public void adjustCellCounts(int fishCellsDelta,
                                 int sharkCellsDelta) {
        fishCells += fishCellsDelta;
        sharkCells += sharkCellsDelta;
    }

    void incrementFishCells() {
        fishCells++;
    }

    void decrementFishCells() {
        fishCells--;
    }

    void incrementSharkCells() {
        sharkCells++;
    }

    void decrementSharkCells() {
        sharkCells--;
    }

    public int fishCells() {
        return fishCells;
    }

    public int sharkCells() {
        return sharkCells;
    }

    @Override
    public String toString() {
        return "WatorStatistics{" +
                baseToString() +
                ", fishCells=" + fishCells +
                ", sharkCells=" + sharkCells +
                '}';
    }

}
