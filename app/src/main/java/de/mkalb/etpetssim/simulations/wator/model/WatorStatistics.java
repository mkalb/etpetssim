package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

public final class WatorStatistics
        extends AbstractTimedSimulationStatistics {

    private int maxFishCells;
    private int maxSharkCells;
    private int minFishCells;
    private int minSharkCells;
    private int fishCells;
    private int sharkCells;

    public WatorStatistics(int totalCells) {
        super(totalCells);
        maxFishCells = 0;
        maxSharkCells = 0;
        minFishCells = totalCells;
        minSharkCells = totalCells;
        fishCells = 0;
        sharkCells = 0;
    }

    public void update(int newStepCount,
                       StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    void updateCells() {
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

    public void incrementFishCells() {
        fishCells++;
    }

    public void decrementFishCells() {
        fishCells--;
    }

    public void incrementSharkCells() {
        sharkCells++;
    }

    public void decrementSharkCells() {
        sharkCells--;
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
