package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridTopologyTest {

    @Test
    void testValidConstruction() {
        GridTopology topology = new GridTopology(CellShape.HEXAGON, BoundaryType.WRAP_X_WRAP_Y);
        assertEquals(CellShape.HEXAGON, topology.cellShape());
        assertEquals(BoundaryType.WRAP_X_WRAP_Y, topology.boundaryType());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullArguments() {
        assertThrows(NullPointerException.class, () -> new GridTopology(null, BoundaryType.BLOCK_X_BLOCK_Y));
        assertThrows(NullPointerException.class, () -> new GridTopology(CellShape.SQUARE, null));
    }

    @Test
    void testEdgeBehaviors() {
        GridTopology topology = new GridTopology(CellShape.TRIANGLE, BoundaryType.REFLECT_XY);
        assertEquals(EdgeBehavior.REFLECT, topology.edgeBehaviorX());
        assertEquals(EdgeBehavior.REFLECT, topology.edgeBehaviorY());
    }

    @Test
    void testVertexCount() {
        assertEquals(3, new GridTopology(CellShape.TRIANGLE, BoundaryType.BLOCK_X_BLOCK_Y).vertexCount());
        assertEquals(4, new GridTopology(CellShape.SQUARE, BoundaryType.BLOCK_X_BLOCK_Y).vertexCount());
        assertEquals(6, new GridTopology(CellShape.HEXAGON, BoundaryType.BLOCK_X_BLOCK_Y).vertexCount());
    }

    @Test
    void testAsStringFormat() {
        assertEquals("[SQUARE BLOCK/WRAP]", new GridTopology(CellShape.SQUARE, BoundaryType.BLOCK_X_WRAP_Y).asString());
        assertEquals("[HEXAGON WRAP/WRAP]", new GridTopology(CellShape.HEXAGON, BoundaryType.WRAP_X_WRAP_Y).asString());
        assertEquals("[TRIANGLE ABSORB/ABSORB]", new GridTopology(CellShape.TRIANGLE, BoundaryType.ABSORB_XY).asString());
    }

}