package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridTopologyTest {

    @Test
    void testValidConstruction() {
        GridTopology topology = new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_WRAP_Y);
        assertEquals(CellShape.HEXAGON, topology.cellShape());
        assertEquals(GridEdgeBehavior.WRAP_X_WRAP_Y, topology.gridEdgeBehavior());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullArguments() {
        assertThrows(NullPointerException.class, () -> new GridTopology(null, GridEdgeBehavior.BLOCK_X_BLOCK_Y));
        assertThrows(NullPointerException.class, () -> new GridTopology(CellShape.SQUARE, null));
    }

    @Test
    void testEdgeBehaviors() {
        GridTopology topology = new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.REFLECT_XY);
        assertEquals(EdgeBehavior.REFLECT, topology.edgeBehaviorX());
        assertEquals(EdgeBehavior.REFLECT, topology.edgeBehaviorY());
    }

    @Test
    void testCellVertexCount() {
        assertEquals(3, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_X_BLOCK_Y).cellVertexCount());
        assertEquals(4, new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y).cellVertexCount());
        assertEquals(6, new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_X_BLOCK_Y).cellVertexCount());

        assertEquals(3, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.REFLECT_XY).cellVertexCount());
        assertEquals(4, new GridTopology(CellShape.SQUARE, GridEdgeBehavior.REFLECT_XY).cellVertexCount());
        assertEquals(6, new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.REFLECT_XY).cellVertexCount());
    }

    @Test
    void testToDisplayStringFormat() {
        assertEquals("[SQUARE BLOCK/WRAP]", new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y).toDisplayString());
        assertEquals("[HEXAGON WRAP/BLOCK]", new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_BLOCK_Y).toDisplayString());
        assertEquals("[HEXAGON WRAP]", new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_WRAP_Y).toDisplayString());
        assertEquals("[TRIANGLE ABSORB]", new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY).toDisplayString());
        assertEquals("[SQUARE REFLECT]", new GridTopology(CellShape.SQUARE, GridEdgeBehavior.REFLECT_XY).toDisplayString());
    }

}