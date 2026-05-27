package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;

public final class LabStatistics implements SimulationStatistics {

    private final GridStructure gridStructure;

    private final int stepCount;

    public LabStatistics(GridStructure gridStructure) {
        this.gridStructure = gridStructure;
        stepCount = 0;
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }

    @Override
    public GridStructure getGridStructure() {
        return gridStructure;
    }

    @Override
    public String toString() {
        return "LabStatistics{" +
                "gridStructure=" + gridStructure +
                ", stepCount=" + stepCount +
                '}';
    }

}
