package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Statistics of Sugarscape with the resource and agent cell counts.
 */
public final class SugarStatistics extends BaseSimulationStatistics {

    public static final String KEY_RESOURCE_CELLS = "resourceCells";
    public static final String KEY_AGENT_CELLS = "agentCells";

    private static final String SUGAR_OBSERVATION_CELLS_RESOURCE = "sugar.observation.cells.resource";
    private static final String SUGAR_OBSERVATION_CELLS_AGENT = "sugar.observation.cells.agent";

    private static final List<StatisticsMetric<SugarStatistics>> METRICS = List.of(
            new StatisticsMetric<>(KEY_RESOURCE_CELLS, SUGAR_OBSERVATION_CELLS_RESOURCE,
                    SugarStatistics::resourceCells,
                    StatisticsExtremaMode.NONE),
            new StatisticsMetric<>(KEY_AGENT_CELLS, SUGAR_OBSERVATION_CELLS_AGENT,
                    SugarStatistics::agentCells,
                    StatisticsExtremaMode.NONE)
    );

    private int resourceCells;
    private int agentCells;

    public SugarStatistics(GridStructure gridStructure) {
        super(gridStructure);
    }

    public static List<StatisticsMetric<SugarStatistics>> metrics() {
        return METRICS;
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

    void initializeStartupCellCounts(int resourceCellsInitial,
                                     int agentCellsInitial) {
        resourceCells = resourceCellsInitial;
        agentCells = agentCellsInitial;
    }

    void adjustAgentCells(int agentCellsDelta) {
        agentCells += agentCellsDelta;
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
