package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class SugarStatistics
        extends BaseTimedSimulationStatistics {

    public static final String KEY_RESOURCE_CELLS = "resourceCells";
    public static final String KEY_AGENT_CELLS = "agentCells";

    private static final String SUGAR_OBSERVATION_RESOURCE_CELLS = "sugar.observation.cells.resource";
    private static final String SUGAR_OBSERVATION_AGENT_CELLS = "sugar.observation.cells.agent";

    private int resourceCells;
    private int agentCells;

    public SugarStatistics(GridStructure gridStructure) {
        super(gridStructure);
        resourceCells = 0;
        agentCells = 0;
    }

    public static List<StatisticMetric<SugarStatistics>> metrics() {
        return List.of(
                new StatisticMetric<>(KEY_RESOURCE_CELLS, SUGAR_OBSERVATION_RESOURCE_CELLS,
                        SugarStatistics::getResourceCells,
                        StatisticExtremaMode.NONE),
                new StatisticMetric<>(KEY_AGENT_CELLS, SUGAR_OBSERVATION_AGENT_CELLS,
                        SugarStatistics::getAgentCells,
                        StatisticExtremaMode.NONE)
        );
    }

    void initializeStartupCellCounts(int resourceCellsInitial,
                                     int agentCellsInitial) {
        resourceCells = resourceCellsInitial;
        agentCells = agentCellsInitial;
    }

    void adjustAgentsCells(int agentCellsDelta) {
        agentCells += agentCellsDelta;
    }

    public void adjustResourceCells(int resourceCellsDelta) {
        resourceCells += resourceCellsDelta;
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
