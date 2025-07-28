package de.mkalb.etpetssim.simulations.wator.model;

public final class WatorStatistics {

    private final long totalCells;

    private long step;
    private long fishCells;
    private long sharkCells;

    public WatorStatistics(long totalCells) {
        this.totalCells = totalCells;
        step = 0;
        fishCells = 0;
        sharkCells = 0;
    }

    public void update(long newStep) {
        step = newStep;
    }

    public long getStep() {
        return step;
    }

    public long getFishCells() {
        return fishCells;
    }

    public long getSharkCells() {
        return sharkCells;
    }

    public long getTotalCells() {
        return totalCells;
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
