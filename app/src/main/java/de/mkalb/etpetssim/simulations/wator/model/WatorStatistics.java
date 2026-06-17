package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class WatorStatistics
        extends BaseTimedSimulationStatistics {

    private int maxFishCells;
    private int maxSharkCells;
    private int minFishCells;
    private int minSharkCells;

    private int fishCells;
    private int sharkCells;

    public WatorStatistics(GridStructure gridStructure) {
        super(gridStructure);
        maxFishCells = 0;
        maxSharkCells = 0;
        minFishCells = 0;
        minSharkCells = 0;
        fishCells = 0;
        sharkCells = 0;
    }

    void initializeStartupCellCounts(int fishCellsInitial,
                                     int sharkCellsInitial) {
        maxFishCells = fishCellsInitial;
        maxSharkCells = sharkCellsInitial;
        minFishCells = fishCellsInitial;
        minSharkCells = sharkCellsInitial;
        fishCells = fishCellsInitial;
        sharkCells = sharkCellsInitial;
    }

    public void adjustCellCounts(int fishCellsDelta,
                                 int sharkCellsDelta) {
        fishCells += fishCellsDelta;
        sharkCells += sharkCellsDelta;
        updateMinMaxCells();
    }

    void updateMinMaxCells() {
        if (fishCells > maxFishCells) {
            maxFishCells = fishCells;
        }
        if (sharkCells > maxSharkCells) {
            maxSharkCells = sharkCells;
        }
        if (fishCells < minFishCells) {
            minFishCells = fishCells;
        }
        if (sharkCells < minSharkCells) {
            minSharkCells = sharkCells;
        }
    }

    /**
     * Increments the fish cell count by one.
     * Call {@link #updateMinMaxCells()} after all mutations of a simulation step to keep min/max values consistent.
     */
    void incrementFishCells() {
        fishCells++;
    }

    /**
     * Decrements the fish cell count by one.
     * Call {@link #updateMinMaxCells()} after all mutations of a simulation step to keep min/max values consistent.
     */
    void decrementFishCells() {
        fishCells--;
    }

    /**
     * Increments the shark cell count by one.
     * Call {@link #updateMinMaxCells()} after all mutations of a simulation step to keep min/max values consistent.
     */
    void incrementSharkCells() {
        sharkCells++;
    }

    /**
     * Decrements the shark cell count by one.
     * Call {@link #updateMinMaxCells()} after all mutations of a simulation step to keep min/max values consistent.
     */
    void decrementSharkCells() {
        sharkCells--;
    }

    public int getMaxFishCells() {
        return maxFishCells;
    }

    public int getMaxSharkCells() {
        return maxSharkCells;
    }

    public int getMinFishCells() {
        return minFishCells;
    }

    public int getMinSharkCells() {
        return minSharkCells;
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
                ", maxFishCells=" + maxFishCells +
                ", maxSharkCells=" + maxSharkCells +
                ", minFishCells=" + minFishCells +
                ", minSharkCells=" + minSharkCells +
                ", fishCells=" + fishCells +
                ", sharkCells=" + sharkCells +
                '}';
    }

}
