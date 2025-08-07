package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.simulations.model.SimulationStatistics;

public final class WatorStatistics
        implements SimulationStatistics {

    private final int totalCells;

    private int stepCount;
    private long fishCells;
    private long sharkCells;
    private long currentStepMillis;
    private long timeOutMillis;
    private long minStepMillis;
    private long maxStepMillis;

    public WatorStatistics(int totalCells) {
        this.totalCells = totalCells;
        stepCount = 0;
        fishCells = 0;
        sharkCells = 0;
        currentStepMillis = 0;
        timeOutMillis = 0;
        minStepMillis = 0;
        maxStepMillis = 0;
    }

    public void update(int newStepCount, long currentStepMillis, long timeOutMillis, long minStepMillis, long maxStepMillis) {
        stepCount = newStepCount;
        this.currentStepMillis = currentStepMillis;
        this.timeOutMillis = timeOutMillis;
        this.minStepMillis = minStepMillis;
        this.maxStepMillis = maxStepMillis;
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

    public long currentStepMillis() {
        return currentStepMillis;
    }

    public long timeOutMillis() {
        return timeOutMillis;
    }

    public long minStepMillis() {
        return minStepMillis;
    }

    public long maxStepMillis() {
        return maxStepMillis;
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

}
