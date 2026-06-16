package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class SugarStatistics
        extends BaseTimedSimulationStatistics {

    private int resourceCells;
    private int agentCells;

    public SugarStatistics(GridStructure gridStructure) {
        super(gridStructure);
        resourceCells = 0;
        agentCells = 0;
    }

    void initializeStartupCellCounts(int resourceCellsInitial,
                                     int agentCellsInitial) {
        resourceCells = resourceCellsInitial;
        agentCells = agentCellsInitial;
    }

    void adjustAgentsCells(int agentCellsDelta) {
        agentCells += agentCellsDelta;
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
