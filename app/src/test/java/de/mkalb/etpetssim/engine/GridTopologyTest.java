package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GridTopologyTest {

    @Test
    void testRecord() {
        GridTopology topology = new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY);
        assertEquals(CellShape.HEXAGON, topology.cellShape());
        assertEquals(GridEdgeBehavior.WRAP_XY, topology.gridEdgeBehavior());

        assertTrue(topology.toString().contains("cellShape=HEXAGON"));
        assertTrue(topology.toString().contains("gridEdgeBehavior=WRAP_XY"));
    }

    @Test
    void testToDisplayStringFormat() {
        assertEquals("[SQUARE BLOCK/WRAP]", new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y).toDisplayString());
        assertEquals("[HEXAGON WRAP/BLOCK]", new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_BLOCK_Y).toDisplayString());
        assertEquals("[SQUARE BLOCK]", new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY).toDisplayString());
        assertEquals("[HEXAGON WRAP]", new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY).toDisplayString());
        assertEquals("[TRIANGLE ABSORB]", new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY).toDisplayString());
        assertEquals("[SQUARE REFLECT]", new GridTopology(CellShape.SQUARE, GridEdgeBehavior.REFLECT_XY).toDisplayString());
    }

    @Test
    void testStaticRequiredWidthMultiple() {
        assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.TRIANGLE, EdgeBehavior.BLOCK));
        assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.SQUARE, EdgeBehavior.BLOCK));
        assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.HEXAGON, EdgeBehavior.BLOCK));

        assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.TRIANGLE, EdgeBehavior.ABSORB));
        assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.SQUARE, EdgeBehavior.ABSORB));
        assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.HEXAGON, EdgeBehavior.ABSORB));

        assertEquals(2, GridTopology.requiredWidthMultiple(CellShape.TRIANGLE, EdgeBehavior.WRAP));
        assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.SQUARE, EdgeBehavior.WRAP));
        assertEquals(2, GridTopology.requiredWidthMultiple(CellShape.HEXAGON, EdgeBehavior.WRAP));
    }

    @Test
    void testStaticRequiredHeightMultiple() {
        assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.TRIANGLE, EdgeBehavior.BLOCK));
        assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.SQUARE, EdgeBehavior.BLOCK));
        assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.HEXAGON, EdgeBehavior.BLOCK));

        assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.TRIANGLE, EdgeBehavior.ABSORB));
        assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.SQUARE, EdgeBehavior.ABSORB));
        assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.HEXAGON, EdgeBehavior.ABSORB));

        assertEquals(2, GridTopology.requiredHeightMultiple(CellShape.TRIANGLE, EdgeBehavior.WRAP));
        assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.SQUARE, EdgeBehavior.WRAP));
        assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.HEXAGON, EdgeBehavior.WRAP));
    }

    @Test
    void testRequiredWidthMultiple() {
        GridTopology topology = new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY);
        assertEquals(2, topology.requiredWidthMultiple());
    }

    @Test
    void testRequiredHeightMultiple() {
        GridTopology topology = new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY);
        assertEquals(2, topology.requiredHeightMultiple());
    }

    @Test
    void testEdgeBehaviorX() {
        assertEquals(EdgeBehavior.BLOCK, new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y).edgeBehaviorX());
        assertEquals(EdgeBehavior.REFLECT, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.REFLECT_XY).edgeBehaviorX());
        assertEquals(EdgeBehavior.BLOCK, new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_X_WRAP_Y).edgeBehaviorX());
    }

    @Test
    void testEdgeBehaviorY() {
        assertEquals(EdgeBehavior.WRAP, new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y).edgeBehaviorY());
        assertEquals(EdgeBehavior.REFLECT, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.REFLECT_XY).edgeBehaviorY());
        assertEquals(EdgeBehavior.WRAP, new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_X_WRAP_Y).edgeBehaviorY());
    }

    @Test
    void testCellVertexCount() {
        assertEquals(3, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_XY).cellVertexCount());
        assertEquals(4, new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY).cellVertexCount());
        assertEquals(6, new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_XY).cellVertexCount());

        assertEquals(3, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.REFLECT_XY).cellVertexCount());
        assertEquals(4, new GridTopology(CellShape.SQUARE, GridEdgeBehavior.REFLECT_XY).cellVertexCount());
        assertEquals(6, new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.REFLECT_XY).cellVertexCount());
    }

    @Test
    void testToDisplayString() {
        assertEquals("[SQUARE ABSORB]", new GridTopology(CellShape.SQUARE, GridEdgeBehavior.ABSORB_XY).toDisplayString());
        assertEquals("[HEXAGON BLOCK/WRAP]", new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_X_WRAP_Y).toDisplayString());
    }

}
