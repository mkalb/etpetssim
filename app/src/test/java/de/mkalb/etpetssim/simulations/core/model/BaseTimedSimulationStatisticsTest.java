package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class BaseTimedSimulationStatisticsTest {

    private static final GridStructure STRUCTURE = new GridStructure(
            new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
            new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE));
    private static final StepTimingStatistics TIMING = new StepTimingStatistics(20, 10, 30, 60, 20);

    @Test
    void testConstructor() {
        var statistics = new BaseTimedSimulationStatistics(STRUCTURE);

        assertAll(
                () -> assertEquals(0, statistics.stepCount()),
                () -> assertSame(STRUCTURE, statistics.gridStructure()),
                () -> assertEquals(StepTimingStatistics.empty(), statistics.stepTimingStatistics())
        );
    }

    @Test
    void testSetStepData() {
        var statistics = new BaseTimedSimulationStatistics(STRUCTURE);

        statistics.setStepData(3, TIMING);

        assertAll(
                () -> assertEquals(3, statistics.stepCount()),
                () -> assertSame(TIMING, statistics.stepTimingStatistics())
        );
    }

    @Test
    void testSetStepDataRejectsNegativeStepCount() {
        var statistics = new BaseTimedSimulationStatistics(STRUCTURE);

        assertThrows(IllegalArgumentException.class, () -> statistics.setStepData(-1, TIMING));
    }

    @Test
    void testBaseToString() {
        var statistics = new BaseTimedSimulationStatistics(STRUCTURE);
        statistics.setStepData(3, TIMING);

        assertEquals("gridStructure=" + STRUCTURE + ", stepCount=3, stepTimingStatistics=" + TIMING,
                statistics.baseToString());
    }

}
