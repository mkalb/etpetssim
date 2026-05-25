package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationConfigTest {

    private static final double CELL_EDGE_LENGTH = 4.0d;
    private static final int GRID_WIDTH_SAMPLE = 11;
    private static final int GRID_HEIGHT_SAMPLE = 13;
    private static final int GRID_CELL_COUNT_SAMPLE = GRID_WIDTH_SAMPLE * GRID_HEIGHT_SAMPLE;
    private static final double RANGE_MIN = 1.0d;
    private static final double RANGE_MAX = 2.0d;
    private static final double RANGE_OUTSIDE = 2.1d;

    private static TestSimulationConfig createConfig(int gridWidth, int gridHeight, double cellEdgeLength) {
        return new TestSimulationConfig(
                CellShape.SQUARE,
                GridEdgeBehavior.BLOCK_XY,
                gridWidth,
                gridHeight,
                cellEdgeLength,
                CellDisplayMode.SHAPE,
                1L,
                NeighborhoodMode.EDGES_ONLY);
    }

    @Test
    void testCreateDerivedGridValues() {
        TestSimulationConfig config = createConfig(GridSize.MIN_SIZE, GridSize.MIN_SIZE + 1, CELL_EDGE_LENGTH);

        GridTopology topology = config.createGridTopology();
        GridSize size = config.createGridSize();
        GridStructure structure = config.createGridStructure();

        assertAll(
                () -> assertEquals(config.cellShape(), topology.cellShape()),
                () -> assertEquals(config.gridEdgeBehavior(), topology.gridEdgeBehavior()),
                () -> assertEquals(config.gridWidth(), size.width()),
                () -> assertEquals(config.gridHeight(), size.height()),
                () -> assertEquals(topology, structure.topology()),
                () -> assertEquals(size, structure.size())
        );
    }

    @Test
    void testComputeCellCountReturnsProduct() {
        TestSimulationConfig config = createConfig(GRID_WIDTH_SAMPLE, GRID_HEIGHT_SAMPLE, CELL_EDGE_LENGTH);

        assertEquals(GRID_CELL_COUNT_SAMPLE, config.computeCellCount());
    }

    @Test
    void testComputeCellCountRejectsOverflow() {
        TestSimulationConfig config = createConfig(Integer.MAX_VALUE, 2, CELL_EDGE_LENGTH);

        assertThrows(ArithmeticException.class, config::computeCellCount);
    }

    @Test
    void testIsInRangeUsesInclusiveBounds() {
        TestSimulationConfig config = createConfig(GridSize.MIN_SIZE, GridSize.MIN_SIZE, CELL_EDGE_LENGTH);

        assertAll(
                () -> assertTrue(config.isInRange(RANGE_MIN, RANGE_MIN, RANGE_MAX)),
                () -> assertTrue(config.isInRange(RANGE_MAX, RANGE_MIN, RANGE_MAX)),
                () -> assertFalse(config.isInRange(RANGE_OUTSIDE, RANGE_MIN, RANGE_MAX))
        );
    }

    @Test
    void testIsValidRejectsInvalidBaseSettings() {
        TestSimulationConfig invalidSize = createConfig(GridSize.MIN_SIZE - 1, GridSize.MIN_SIZE, CELL_EDGE_LENGTH);
        TestSimulationConfig invalidEdgeLength = createConfig(GridSize.MIN_SIZE, GridSize.MIN_SIZE, 0.0d);

        assertAll(
                () -> assertFalse(invalidSize.isValid()),
                () -> assertFalse(invalidEdgeLength.isValid())
        );
    }

    private record TestSimulationConfig(
            CellShape cellShape,
            GridEdgeBehavior gridEdgeBehavior,
            int gridWidth,
            int gridHeight,
            double cellEdgeLength,
            CellDisplayMode cellDisplayMode,
            long seed,
            NeighborhoodMode neighborhoodMode)
            implements SimulationConfig {
    }

}

