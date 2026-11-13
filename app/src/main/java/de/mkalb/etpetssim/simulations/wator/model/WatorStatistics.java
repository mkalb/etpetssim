package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsChartGroup;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Statistics of Wa-Tor with the fish and shark cell counts.
 */
public final class WatorStatistics extends BaseSimulationStatistics {

    public static final String KEY_FISH_CELLS = "fishCells";
    public static final String KEY_SHARK_CELLS = "sharkCells";

    private static final String WATOR_OBSERVATION_CELLS_FISH = "wator.observation.cells.fish";
    private static final String WATOR_OBSERVATION_CELLS_SHARK = "wator.observation.cells.shark";
    private static final int CHART_WINDOW_SIZE = 500;

    private static final List<StatisticsMetric<WatorStatistics>> METRICS = List.of(
            new StatisticsMetric<>(KEY_FISH_CELLS, WATOR_OBSERVATION_CELLS_FISH,
                    WatorStatistics::fishCells,
                    StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY, CHART_WINDOW_SIZE),
            new StatisticsMetric<>(KEY_SHARK_CELLS, WATOR_OBSERVATION_CELLS_SHARK,
                    WatorStatistics::sharkCells,
                    StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY, CHART_WINDOW_SIZE)
    );

    private int fishCells;
    private int sharkCells;

    public WatorStatistics(GridStructure gridStructure) {
        super(gridStructure);
    }

    public static List<StatisticsMetric<WatorStatistics>> metrics() {
        return METRICS;
    }

    public void adjustCellCounts(int fishCellsDelta,
                                 int sharkCellsDelta) {
        fishCells += fishCellsDelta;
        sharkCells += sharkCellsDelta;
    }

    public int fishCells() {
        return fishCells;
    }

    public int sharkCells() {
        return sharkCells;
    }

    void initializeStartupCellCounts(int fishCellsInitial,
                                     int sharkCellsInitial) {
        fishCells = fishCellsInitial;
        sharkCells = sharkCellsInitial;
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

    @Override
    public String toString() {
        return "WatorStatistics{" +
                baseToString() +
                ", fishCells=" + fishCells +
                ", sharkCells=" + sharkCells +
                '}';
    }

}
