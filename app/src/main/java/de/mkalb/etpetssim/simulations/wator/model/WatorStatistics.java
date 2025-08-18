package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationStatistics;

public final class WatorStatistics
        extends AbstractTimedSimulationStatistics {

    private long fishCells;
    private long sharkCells;

    public WatorStatistics(int totalCells) {
        super(totalCells);
        fishCells = 0;
        sharkCells = 0;
    }

    public void update(int newStepCount,
                       StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    public long getFishCells() {
        return fishCells;
    }

    public long getSharkCells() {
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
                ", fishCells=" + fishCells +
                ", sharkCells=" + sharkCells +
                '}';
    }

}
