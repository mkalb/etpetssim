package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationConfigTest {

    private static final double CELL_EDGE_LENGTH = 4.0d;
    private static final int GRID_WIDTH_SAMPLE = 11;
    private static final int GRID_HEIGHT_SAMPLE = 13;
    private static final int GRID_CELL_COUNT_SAMPLE = GRID_WIDTH_SAMPLE * GRID_HEIGHT_SAMPLE;
    private static final double DOUBLE_RANGE_MIN = 1.0d;
    private static final double DOUBLE_RANGE_MAX = 2.0d;
    private static final double DOUBLE_RANGE_BELOW_MIN = 0.9d;
    private static final double DOUBLE_RANGE_ABOVE_MAX = 2.1d;
    private static final int INT_RANGE_MIN = 1;
    private static final int INT_RANGE_MAX = 3;
    private static final int INT_RANGE_BELOW_MIN = 0;
    private static final int INT_RANGE_ABOVE_MAX = 4;

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
    void testIsInRangeDoubleUsesInclusiveBounds() {
        TestSimulationConfig config = createConfig(GridSize.MIN_SIZE, GridSize.MIN_SIZE, CELL_EDGE_LENGTH);

        assertAll(
                () -> assertTrue(config.isInRangeDouble(DOUBLE_RANGE_MIN, DOUBLE_RANGE_MIN, DOUBLE_RANGE_MAX)),
                () -> assertTrue(config.isInRangeDouble(DOUBLE_RANGE_MAX, DOUBLE_RANGE_MIN, DOUBLE_RANGE_MAX)),
                () -> assertFalse(config.isInRangeDouble(DOUBLE_RANGE_BELOW_MIN, DOUBLE_RANGE_MIN, DOUBLE_RANGE_MAX)),
                () -> assertFalse(config.isInRangeDouble(DOUBLE_RANGE_ABOVE_MAX, DOUBLE_RANGE_MIN, DOUBLE_RANGE_MAX))
        );
    }

    @Test
    void testIsInRangeIntUsesInclusiveBounds() {
        TestSimulationConfig config = createConfig(GridSize.MIN_SIZE, GridSize.MIN_SIZE, CELL_EDGE_LENGTH);

        assertAll(
                () -> assertTrue(config.isInRangeInt(INT_RANGE_MIN, INT_RANGE_MIN, INT_RANGE_MAX)),
                () -> assertTrue(config.isInRangeInt(INT_RANGE_MAX, INT_RANGE_MIN, INT_RANGE_MAX)),
                () -> assertFalse(config.isInRangeInt(INT_RANGE_BELOW_MIN, INT_RANGE_MIN, INT_RANGE_MAX)),
                () -> assertFalse(config.isInRangeInt(INT_RANGE_ABOVE_MAX, INT_RANGE_MIN, INT_RANGE_MAX))
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

    @Test
    void testIsValidDelegatesToBaseValidation() {
        TestSimulationConfig validConfig = createConfig(GridSize.MIN_SIZE, GridSize.MIN_SIZE, CELL_EDGE_LENGTH);
        TestSimulationConfig invalidConfig = createConfig(GridSize.MIN_SIZE - 1, GridSize.MIN_SIZE, CELL_EDGE_LENGTH);

        assertAll(
                () -> assertTrue(validConfig.isBaseValid()),
                () -> assertTrue(validConfig.isValid()),
                () -> assertFalse(invalidConfig.isBaseValid()),
                () -> assertFalse(invalidConfig.isValid())
        );
    }

    @Test
    void testHasAllowedCoreSelectionsChecksConfiguredCoreValues() {
        TestSimulationConfig config = createConfig(GridSize.MIN_SIZE, GridSize.MIN_SIZE, CELL_EDGE_LENGTH);

        assertAll(
                () -> assertTrue(config.hasAllowedCoreSelections(
                        List.of(CellShape.SQUARE, CellShape.HEXAGON),
                        List.of(GridEdgeBehavior.BLOCK_XY),
                        List.of(CellDisplayMode.SHAPE)
                )),
                () -> assertFalse(config.hasAllowedCoreSelections(
                        List.of(CellShape.HEXAGON),
                        List.of(GridEdgeBehavior.BLOCK_XY),
                        List.of(CellDisplayMode.SHAPE)
                ))
        );
    }

    @Test
    void testIsAllowedSelectionUsesMembership() {
        TestSimulationConfig config = createConfig(GridSize.MIN_SIZE, GridSize.MIN_SIZE, CELL_EDGE_LENGTH);

        assertAll(
                () -> assertTrue(config.isAllowedSelection(NeighborhoodMode.EDGES_ONLY,
                        List.of(NeighborhoodMode.EDGES_ONLY, NeighborhoodMode.EDGES_AND_VERTICES))),
                () -> assertFalse(config.isAllowedSelection(NeighborhoodMode.EDGES_ONLY,
                        List.of(NeighborhoodMode.EDGES_AND_VERTICES)))
        );
    }

    @Test
    void testHasExpectedSelectionComparesConfiguredAndExpectedValues() {
        TestSimulationConfig config = createConfig(GridSize.MIN_SIZE, GridSize.MIN_SIZE, CELL_EDGE_LENGTH);

        assertAll(
                () -> assertTrue(config.hasExpectedSelection(config.neighborhoodMode(), NeighborhoodMode.EDGES_ONLY)),
                () -> assertFalse(config.hasExpectedSelection(config.neighborhoodMode(), NeighborhoodMode.EDGES_AND_VERTICES)),
                () -> assertTrue(config.hasExpectedSelection(5, 5)),
                () -> assertFalse(config.hasExpectedSelection(5, 6))
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

