package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Statistics of Rebounding Entities with the wall and rebounder cell counts.
 */
public final class ReboundingStatistics extends BaseTimedSimulationStatistics {

    public static final String KEY_WALL_CELLS = "wallCells";
    public static final String KEY_REBOUNDER_CELLS = "rebounderCells";

    private static final String REBOUNDING_OBSERVATION_CELLS_WALL = "rebounding.observation.cells.wall";
    private static final String REBOUNDING_OBSERVATION_CELLS_REBOUNDER = "rebounding.observation.cells.rebounder";

    private static final List<StatisticsMetric<ReboundingStatistics>> METRICS = List.of(
            new StatisticsMetric<>(KEY_WALL_CELLS, REBOUNDING_OBSERVATION_CELLS_WALL,
                    ReboundingStatistics::wallCells,
                    StatisticsExtremaMode.MAX),
            new StatisticsMetric<>(KEY_REBOUNDER_CELLS, REBOUNDING_OBSERVATION_CELLS_REBOUNDER,
                    ReboundingStatistics::rebounderCells,
                    StatisticsExtremaMode.MAX)
    );

    private int wallCells;
    private int rebounderCells;

    public ReboundingStatistics(GridStructure gridStructure) {
        super(gridStructure);
    }

    public static List<StatisticsMetric<ReboundingStatistics>> metrics() {
        return METRICS;
    }

    public int wallCells() {
        return wallCells;
    }

    public int rebounderCells() {
        return rebounderCells;
    }

    void initializeStartupCellCounts(int wallCellsInitial,
                                     int rebounderCellsInitial) {
        wallCells = wallCellsInitial;
        rebounderCells = rebounderCellsInitial;
    }

    void incrementWallCells() {
        wallCells++;
    }

    void decrementWallCells() {
        wallCells--;
    }

    void incrementRebounderCells() {
        rebounderCells++;
    }

    void decrementRebounderCells() {
        rebounderCells--;
    }

    @Override
    public String toString() {
        return "ReboundingStatistics{" +
                baseToString() +
                ", wallCells=" + wallCells +
                ", rebounderCells=" + rebounderCells +
                '}';
    }

}
