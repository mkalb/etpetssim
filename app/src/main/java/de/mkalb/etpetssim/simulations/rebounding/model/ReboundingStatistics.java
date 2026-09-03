package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class ReboundingStatistics
        extends BaseTimedSimulationStatistics {

    public static final String KEY_WALL_CELLS = "wallCells";
    public static final String KEY_MOVING_ENTITY_CELLS = "movingEntityCells";

    private static final String REBOUNDING_OBSERVATION_WALL_CELLS = "rebounding.observation.cells.wall";
    private static final String REBOUNDING_OBSERVATION_MOVING_ENTITY_CELLS = "rebounding.observation.cells.movingentity";

    private int wallCells;
    private int movingEntityCells;

    public ReboundingStatistics(GridStructure gridStructure) {
        super(gridStructure);
        wallCells = 0;
        movingEntityCells = 0;
    }

    public static List<StatisticMetric<ReboundingStatistics>> metrics() {
        return List.of(
                new StatisticMetric<>(KEY_WALL_CELLS, REBOUNDING_OBSERVATION_WALL_CELLS,
                        ReboundingStatistics::getWallCells,
                        StatisticExtremaMode.MAX),
                new StatisticMetric<>(KEY_MOVING_ENTITY_CELLS, REBOUNDING_OBSERVATION_MOVING_ENTITY_CELLS,
                        ReboundingStatistics::getMovingEntityCells,
                        StatisticExtremaMode.MAX)
        );
    }

    void initializeStartupCellCounts(int wallCellsInitial,
                                     int movingEntityCellsInitial) {
        wallCells = wallCellsInitial;
        movingEntityCells = movingEntityCellsInitial;
    }

    public void incrementWallCells() {
        wallCells += 1;
    }

    public void decrementWallCells() {
        wallCells -= 1;
    }

    public void decrementMovingEntityCells() {
        movingEntityCells -= 1;
    }

    public void incrementMovingEntityCells() {
        movingEntityCells += 1;
    }

    public int getWallCells() {
        return wallCells;
    }

    public int getMovingEntityCells() {
        return movingEntityCells;
    }

    @Override
    public String toString() {
        return "ReboundingStatistics{" +
                baseToString() +
                ", wallCells=" + wallCells +
                ", movingEntityCells=" + movingEntityCells +
                '}';
    }

}
