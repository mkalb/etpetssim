package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.model.TimedSimulationStatistics;

public final class WatorStatistics
        implements TimedSimulationStatistics {

    private final int totalCells;

    private int stepCount;
    private long fishCells;
    private long sharkCells;
    private long timeOutMillis;
    private StepTimingStatistics stepTimingStatistics;

    public WatorStatistics(int totalCells) {
        this.totalCells = totalCells;
        stepCount = 0;
        fishCells = 0;
        sharkCells = 0;
        timeOutMillis = 0;
        stepTimingStatistics = StepTimingStatistics.empty();
    }

    public void update(int newStepCount, long newTimeOutMillis, StepTimingStatistics newStepTimingStatistics) {
        stepCount = newStepCount;
        timeOutMillis = newTimeOutMillis;
        stepTimingStatistics = newStepTimingStatistics;
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }

    public long getFishCells() {
        return fishCells;
    }

    public long getSharkCells() {
        return sharkCells;
    }

    @Override
    public int getTotalCells() {
        return totalCells;
    }

    @Override
    public long timeOutMillis() {
        return timeOutMillis;
    }

    @Override
    public StepTimingStatistics stepTimingStatistics() {
        return stepTimingStatistics;
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
                "totalCells=" + totalCells +
                ", stepCount=" + stepCount +
                ", fishCells=" + fishCells +
                ", sharkCells=" + sharkCells +
                ", timeOutMillis=" + timeOutMillis +
                ", stepTimingStatistics=" + stepTimingStatistics +
                '}';
    }

}
