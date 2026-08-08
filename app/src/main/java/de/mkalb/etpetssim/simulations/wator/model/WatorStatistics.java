package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class WatorStatistics
        extends BaseTimedSimulationStatistics {

    private static final String WATOR_OBSERVATION_FISH_CELLS = "wator.observation.cells.fish";
    private static final String WATOR_OBSERVATION_SHARK_CELLS = "wator.observation.cells.shark";

    private int fishCells;
    private int sharkCells;

    public WatorStatistics(GridStructure gridStructure) {
        super(gridStructure);
        fishCells = 0;
        sharkCells = 0;
    }

    public static List<StatisticMetric<WatorStatistics>> metrics() {
        return List.of(
                new StatisticMetric<>("fishCells", WATOR_OBSERVATION_FISH_CELLS, WatorStatistics::getFishCells,
                        StatisticExtremaMode.MIN_AND_MAX),
                new StatisticMetric<>("sharkCells", WATOR_OBSERVATION_SHARK_CELLS, WatorStatistics::getSharkCells,
                        StatisticExtremaMode.MIN_AND_MAX)
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

    /**
     * Increments the fish cell count by one.
     */
    void incrementFishCells() {
        fishCells++;
    }

    /**
     * Decrements the fish cell count by one.
     */
    void decrementFishCells() {
        fishCells--;
    }

    /**
     * Increments the shark cell count by one.
     */
    void incrementSharkCells() {
        sharkCells++;
    }

    /**
     * Decrements the shark cell count by one.
     */
    void decrementSharkCells() {
        sharkCells--;
    }

    public int getFishCells() {
        return fishCells;
    }

    public int getSharkCells() {
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
