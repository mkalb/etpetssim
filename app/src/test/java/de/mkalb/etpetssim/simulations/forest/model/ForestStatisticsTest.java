package de.mkalb.etpetssim.simulations.forest.model;

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

final class ForestStatisticsTest {

    private static final double DELTA = 1.0e-9d;

    private static ForestStatistics createStatistics() {
        return new ForestStatistics(new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE * 2)));
    }

    @Test
    void testConstructor() {
        ForestStatistics statistics = createStatistics();

        assertAll(
                () -> assertEquals(GridSize.MIN_SIZE * GridSize.MIN_SIZE * 2, statistics.totalCells()),
                () -> assertEquals(statistics.totalCells(), statistics.emptyCells()),
                () -> assertEquals(0, statistics.treeCells()),
                () -> assertEquals(0, statistics.burningCells()),
                () -> assertEquals(0, statistics.cumulativeLightningStrikeCount()),
                () -> assertEquals(0, statistics.cumulativeBurnedTreeCount())
        );
    }

    @Test
    void testMetrics() {
        ForestStatistics statistics = createStatistics();
        statistics.updateStepCounts(5, 2, 1, 3);

        List<StatisticsMetric<ForestStatistics>> metrics = ForestStatistics.metrics();

        assertAll(
                () -> assertEquals(List.of(ForestStatistics.KEY_EMPTY_CELLS, ForestStatistics.KEY_TREE_CELLS,
                                ForestStatistics.KEY_BURNING_CELLS,
                                ForestStatistics.KEY_CUMULATIVE_LIGHTNING_STRIKE_COUNT,
                                ForestStatistics.KEY_CUMULATIVE_BURNED_TREE_COUNT),
                        metrics.stream().map(StatisticsMetric::key).toList()),
                () -> assertEquals(statistics.totalCells() - 7.0d,
                        metrics.getFirst().extractor().applyAsDouble(statistics), DELTA),
                () -> assertEquals(5.0d, metrics.get(1).extractor().applyAsDouble(statistics), DELTA),
                () -> assertEquals(2.0d, metrics.get(2).extractor().applyAsDouble(statistics), DELTA),
                () -> assertEquals(1.0d, metrics.get(3).extractor().applyAsDouble(statistics), DELTA),
                () -> assertEquals(3.0d, metrics.get(4).extractor().applyAsDouble(statistics), DELTA)
        );
    }

    @Test
    void testInitializeStartupCellCounts() {
        ForestStatistics statistics = createStatistics();

        statistics.initializeStartupCellCounts(5);

        assertAll(
                () -> assertEquals(statistics.totalCells() - 5, statistics.emptyCells()),
                () -> assertEquals(5, statistics.treeCells()),
                () -> assertEquals(0, statistics.burningCells())
        );
    }

    @Test
    void testSetCellCounts() {
        ForestStatistics statistics = createStatistics();

        statistics.setCellCounts(5, 2);

        assertAll(
                () -> assertEquals(statistics.totalCells() - 7, statistics.emptyCells()),
                () -> assertEquals(5, statistics.treeCells()),
                () -> assertEquals(2, statistics.burningCells())
        );
    }

    @Test
    void testUpdateStepCounts() {
        ForestStatistics statistics = createStatistics();

        statistics.updateStepCounts(5, 2, 1, 3);
        statistics.updateStepCounts(4, 1, 2, 2);

        assertAll(
                () -> assertEquals(statistics.totalCells() - 5, statistics.emptyCells()),
                () -> assertEquals(4, statistics.treeCells()),
                () -> assertEquals(1, statistics.burningCells()),
                () -> assertEquals(3, statistics.cumulativeLightningStrikeCount()),
                () -> assertEquals(5, statistics.cumulativeBurnedTreeCount())
        );
    }

    @Test
    void testToString() {
        ForestStatistics statistics = createStatistics();
        statistics.updateStepCounts(5, 2, 1, 3);

        String text = statistics.toString();

        assertAll(
                () -> assertTrue(text.startsWith("ForestStatistics{")),
                () -> assertTrue(text.contains("emptyCells=" + (statistics.totalCells() - 7))),
                () -> assertTrue(text.contains("treeCells=5")),
                () -> assertTrue(text.contains("burningCells=2")),
                () -> assertTrue(text.contains("cumulativeLightningStrikeCount=1")),
                () -> assertTrue(text.contains("cumulativeBurnedTreeCount=3"))
        );
    }

}
