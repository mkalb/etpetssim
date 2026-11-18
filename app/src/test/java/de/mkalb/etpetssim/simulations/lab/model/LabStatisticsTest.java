package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LabStatisticsTest {

    private static final double DELTA = 1.0e-9d;

    private static LabStatistics createStatistics() {
        return new LabStatistics(new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE * 2)));
    }

    @Test
    void testConstructor() {
        LabStatistics statistics = createStatistics();

        assertAll(
                () -> assertEquals(GridSize.MIN_SIZE * GridSize.MIN_SIZE * 2, statistics.totalCells()),
                () -> assertEquals(0, statistics.highlightedCells()),
                () -> assertEquals(statistics.totalCells(), statistics.normalCells())
        );
    }

    @Test
    void testMetrics() {
        LabStatistics statistics = createStatistics();
        statistics.setCellCounts(5);

        List<StatisticsMetric<LabStatistics>> metrics = LabStatistics.metrics();

        assertAll(
                () -> assertEquals(List.of(LabStatistics.KEY_HIGHLIGHTED_CELLS, LabStatistics.KEY_NORMAL_CELLS),
                        metrics.stream().map(StatisticsMetric::key).toList()),
                () -> assertEquals(5.0d, metrics.getFirst().extractor().applyAsDouble(statistics), DELTA),
                () -> assertEquals(statistics.totalCells() - 5.0d,
                        metrics.get(1).extractor().applyAsDouble(statistics), DELTA)
        );
    }

    @Test
    void testSetCellCounts() {
        LabStatistics statistics = createStatistics();

        statistics.setCellCounts(5);

        assertAll(
                () -> assertEquals(5, statistics.highlightedCells()),
                () -> assertEquals(statistics.totalCells() - 5, statistics.normalCells())
        );
    }

    @Test
    void testToString() {
        LabStatistics statistics = createStatistics();
        statistics.setCellCounts(5);

        String text = statistics.toString();

        assertAll(
                () -> assertTrue(text.startsWith("LabStatistics{")),
                () -> assertTrue(text.contains("highlightedCells=5")),
                () -> assertTrue(text.contains("normalCells=" + (statistics.totalCells() - 5)))
        );
    }

}
