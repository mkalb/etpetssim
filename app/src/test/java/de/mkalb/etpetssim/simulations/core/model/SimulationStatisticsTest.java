package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SimulationStatisticsTest {

    @Test
    void testTotalCells() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE * 2));

        SimulationStatistics statistics = new TestStatistics(structure);

        assertEquals(structure.cellCount(), statistics.totalCells());
    }

    private record TestStatistics(GridStructure gridStructure) implements SimulationStatistics {

        @Override
        public int stepCount() {
            return 0;
        }

    }

}
