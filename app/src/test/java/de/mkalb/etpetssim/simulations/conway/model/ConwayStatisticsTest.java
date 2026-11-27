package de.mkalb.etpetssim.simulations.conway.model;

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

final class ConwayStatisticsTest {

    private static final double DELTA = 1.0e-9d;

    private static ConwayStatistics createStatistics() {
        return new ConwayStatistics(new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE * 2)));
    }

    @Test
    void testConstructor() {
        ConwayStatistics statistics = createStatistics();

        assertAll(
                () -> assertEquals(GridSize.MIN_SIZE * GridSize.MIN_SIZE * 2, statistics.totalCells()),
                () -> assertEquals(0, statistics.aliveCells()),
                () -> assertEquals(statistics.totalCells(), statistics.deadCells()),
                () -> assertEquals(0, statistics.changedCells())
        );
    }

    @Test
    void testMetrics() {
        ConwayStatistics statistics = createStatistics();
        statistics.setCellCounts(5, 2);

        List<StatisticsMetric<ConwayStatistics>> metrics = ConwayStatistics.metrics();

        assertAll(
                () -> assertEquals(List.of(ConwayStatistics.KEY_ALIVE_CELLS, ConwayStatistics.KEY_DEAD_CELLS,
                                ConwayStatistics.KEY_CHANGED_CELLS),
                        metrics.stream().map(StatisticsMetric::key).toList()),
                () -> assertEquals(5.0d, metrics.getFirst().extractor().applyAsDouble(statistics), DELTA),
                () -> assertEquals(statistics.totalCells() - 5.0d,
                        metrics.get(1).extractor().applyAsDouble(statistics), DELTA),
                () -> assertEquals(2.0d, metrics.get(2).extractor().applyAsDouble(statistics), DELTA)
        );
    }

    @Test
    void testAdjustCellCounts() {
        ConwayStatistics statistics = createStatistics();
        statistics.setCellCounts(5, 2);

        statistics.adjustCellCounts(3, 1);
        statistics.adjustCellCounts(-1, 2);

        assertAll(
                () -> assertEquals(7, statistics.aliveCells()),
                () -> assertEquals(statistics.totalCells() - 7, statistics.deadCells()),
                () -> assertEquals(5, statistics.changedCells())
        );
    }

    @Test
    void testInitializeStartupCellCounts() {
        ConwayStatistics statistics = createStatistics();

        statistics.initializeStartupCellCounts(5);

        assertAll(
                () -> assertEquals(5, statistics.aliveCells()),
                () -> assertEquals(statistics.totalCells() - 5, statistics.deadCells()),
                () -> assertEquals(0, statistics.changedCells())
        );
    }

    @Test
    void testSetCellCounts() {
        ConwayStatistics statistics = createStatistics();
        statistics.setCellCounts(9, 4);

        statistics.setCellCounts(5, 2);

        assertAll(
                () -> assertEquals(5, statistics.aliveCells()),
                () -> assertEquals(statistics.totalCells() - 5, statistics.deadCells()),
                () -> assertEquals(2, statistics.changedCells())
        );
    }

    @Test
    void testToString() {
        ConwayStatistics statistics = createStatistics();
        statistics.setCellCounts(5, 2);

        String text = statistics.toString();

        assertAll(
                () -> assertTrue(text.startsWith("ConwayStatistics{")),
                () -> assertTrue(text.contains("aliveCells=5")),
                () -> assertTrue(text.contains("deadCells=" + (statistics.totalCells() - 5))),
                () -> assertTrue(text.contains("changedCells=2"))
        );
    }

}
