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

    public static List<StatisticsMetric<SugarStatistics>> metrics() {
        return List.of(
                new StatisticsMetric<>(KEY_RESOURCE_CELLS, SUGAR_OBSERVATION_RESOURCE_CELLS,
                        SugarStatistics::resourceCells,
                        StatisticsExtremaMode.NONE),
                new StatisticsMetric<>(KEY_AGENT_CELLS, SUGAR_OBSERVATION_AGENT_CELLS,
                        SugarStatistics::agentCells,
                        StatisticsExtremaMode.NONE)
        );
    }

    void initializeStartupCellCounts(int resourceCellsInitial,
                                     int agentCellsInitial) {
        resourceCells = resourceCellsInitial;
        agentCells = agentCellsInitial;
    }

    void adjustAgentCells(int agentCellsDelta) {
        agentCells += agentCellsDelta;
    }

    public void adjustResourceCells(int resourceCellsDelta) {
        resourceCells += resourceCellsDelta;
    }

    public int resourceCells() {
        return resourceCells;
    }

    public int agentCells() {
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
