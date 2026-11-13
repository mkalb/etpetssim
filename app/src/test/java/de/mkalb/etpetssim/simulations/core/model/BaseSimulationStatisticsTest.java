package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class BaseSimulationStatisticsTest {

    private static final GridStructure STRUCTURE = new GridStructure(
            new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
            new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE));

    @Test
    void testConstructor() {
        var statistics = new BaseSimulationStatistics(STRUCTURE);

        assertAll(
                () -> assertEquals(0, statistics.stepCount()),
                () -> assertSame(STRUCTURE, statistics.gridStructure())
        );
    }

    @Test
    void testSetStepCount() {
        var statistics = new BaseSimulationStatistics(STRUCTURE);

        statistics.setStepCount(3);

        assertEquals(3, statistics.stepCount());
    }

    @Test
    void testSetStepCountRejectsNegativeStepCount() {
        var statistics = new BaseSimulationStatistics(STRUCTURE);

        assertThrows(IllegalArgumentException.class, () -> statistics.setStepCount(-1));
    }

    @Test
    void testBaseToString() {
        var statistics = new BaseSimulationStatistics(STRUCTURE);
        statistics.setStepCount(3);

        assertEquals("gridStructure=" + STRUCTURE + ", stepCount=3", statistics.baseToString());
    }

}
