package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsChartGroup;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Statistics of the Simulation Lab with the highlighted and normal cell counts.
 */
public final class LabStatistics extends BaseSimulationStatistics {

    public static final String KEY_HIGHLIGHTED_CELLS = "highlightedCells";
    public static final String KEY_NORMAL_CELLS = "normalCells";

    private static final String LAB_OBSERVATION_CELLS_HIGHLIGHTED = "lab.observation.cells.highlighted";
    private static final String LAB_OBSERVATION_CELLS_NORMAL = "lab.observation.cells.normal";
    private static final int CHART_WINDOW_SIZE = 50;

    private static final List<StatisticsMetric<LabStatistics>> METRICS = List.of(
            new StatisticsMetric<>(KEY_HIGHLIGHTED_CELLS, LAB_OBSERVATION_CELLS_HIGHLIGHTED,
                    LabStatistics::highlightedCells,
                    StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY, CHART_WINDOW_SIZE),
            new StatisticsMetric<>(KEY_NORMAL_CELLS, LAB_OBSERVATION_CELLS_NORMAL,
                    LabStatistics::normalCells,
                    StatisticsExtremaMode.MIN_AND_MAX)
    );

    private int highlightedCells;
    private int normalCells;

    public LabStatistics(GridStructure gridStructure) {
        super(gridStructure);
        normalCells = totalCells();
    }

    public static List<StatisticsMetric<LabStatistics>> metrics() {
        return METRICS;
    }

    public int highlightedCells() {
        return highlightedCells;
    }

    public int normalCells() {
        return normalCells;
    }

    void setCellCounts(int newHighlightedCells) {
        highlightedCells = newHighlightedCells;
        normalCells = totalCells() - newHighlightedCells;
    }

    @Override
    public String toString() {
        return "LabStatistics{" +
                baseToString() +
                ", highlightedCells=" + highlightedCells +
                ", normalCells=" + normalCells +
                '}';
    }

}
