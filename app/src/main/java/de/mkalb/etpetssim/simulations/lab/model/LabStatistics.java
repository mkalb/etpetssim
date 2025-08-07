package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.simulations.model.SimulationStatistics;

public final class LabStatistics implements SimulationStatistics {

    private final int totalCells;

    private final int stepCount;

    public LabStatistics(int totalCells) {
        this.totalCells = totalCells;
        stepCount = 0;
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }

    @Override
    public int getTotalCells() {
        return totalCells;
    }

}
