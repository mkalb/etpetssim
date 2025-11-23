package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

public final class SugarStatistics
        extends AbstractTimedSimulationStatistics {

    private int resourceCells;
    private int agentCells;

    public SugarStatistics(int totalCells) {
        super(totalCells);
        resourceCells = 0;
        agentCells = 0;
    }

    void update(int newStepCount,
                StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    void updateInitialCells(int resourceCellsInitial,
                            int agentCellsInitial) {
        resourceCells = resourceCellsInitial;
        agentCells = agentCellsInitial;
    }

    void updateCells(int agentCellsChange) {
        agentCells += agentCellsChange;
    }

    public int getResourceCells() {
        return resourceCells;
    }

    public int getAgentCells() {
        return agentCells;
    }

    @Override
    public String toString() {
        return "SugarStatistics{" +
                baseToString() +
                ", resourceCells=" + resourceCells +
                ", agentCells=" + agentCells +
                '}';
    }

}