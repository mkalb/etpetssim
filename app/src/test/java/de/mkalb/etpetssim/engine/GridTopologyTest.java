package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class GridTopologyTest {

    @Test
    void testRequiredMultiplesAreWithinMaximum() {
        for (CellShape cellShape : CellShape.values()) {
            for (EdgeBehavior edgeBehavior : EdgeBehavior.values()) {
                int widthMultiple = GridTopology.requiredWidthMultiple(cellShape, edgeBehavior);
                int heightMultiple = GridTopology.requiredHeightMultiple(cellShape, edgeBehavior);
                assertAll(
                        () -> assertEquals(0, (GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE % widthMultiple), cellShape + " " + edgeBehavior),
                        () -> assertEquals(0, (GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE % heightMultiple), cellShape + " " + edgeBehavior)
                );
            }
        }
    }

    @Test
    void testRequiredWidthMultipleWithCellShape() {
        assertAll(
                () -> assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.TRIANGLE, EdgeBehavior.BLOCK)),
                () -> assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.SQUARE, EdgeBehavior.BLOCK)),
                () -> assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.HEXAGON, EdgeBehavior.BLOCK)),
                () -> assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.TRIANGLE, EdgeBehavior.ABSORB)),
                () -> assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.SQUARE, EdgeBehavior.ABSORB)),
                () -> assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.HEXAGON, EdgeBehavior.ABSORB)),
                () -> assertEquals(2, GridTopology.requiredWidthMultiple(CellShape.TRIANGLE, EdgeBehavior.WRAP)),
                () -> assertEquals(1, GridTopology.requiredWidthMultiple(CellShape.SQUARE, EdgeBehavior.WRAP)),
                () -> assertEquals(2, GridTopology.requiredWidthMultiple(CellShape.HEXAGON, EdgeBehavior.WRAP))
        );
    }

    @Test
    void testRequiredHeightMultipleWithCellShape() {
        assertAll(
                () -> assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.TRIANGLE, EdgeBehavior.BLOCK)),
                () -> assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.SQUARE, EdgeBehavior.BLOCK)),
                () -> assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.HEXAGON, EdgeBehavior.BLOCK)),
                () -> assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.TRIANGLE, EdgeBehavior.ABSORB)),
                () -> assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.SQUARE, EdgeBehavior.ABSORB)),
                () -> assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.HEXAGON, EdgeBehavior.ABSORB)),
                () -> assertEquals(2, GridTopology.requiredHeightMultiple(CellShape.TRIANGLE, EdgeBehavior.WRAP)),
                () -> assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.SQUARE, EdgeBehavior.WRAP)),
                () -> assertEquals(1, GridTopology.requiredHeightMultiple(CellShape.HEXAGON, EdgeBehavior.WRAP))
        );
    }

    @Test
    void testRequiredWidthMultiple() {
        assertAll(
                () -> assertEquals(2, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY)
                        .requiredWidthMultiple()),
                () -> assertEquals(1, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_X_WRAP_Y)
                        .requiredWidthMultiple())
        );
    }

    @Test
    void testRequiredHeightMultiple() {
        assertAll(
                () -> assertEquals(2, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY)
                        .requiredHeightMultiple()),
                () -> assertEquals(1, new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_BLOCK_Y)
                        .requiredHeightMultiple())
        );
    }

    @Test
    void testEdgeBehaviorX() {
        assertAll(
                () -> assertEquals(EdgeBehavior.BLOCK,
                        new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y).edgeBehaviorX()),
                () -> assertEquals(EdgeBehavior.WRAP,
                        new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY).edgeBehaviorX())
        );
    }

    @Test
    void testEdgeBehaviorY() {
        assertAll(
                () -> assertEquals(EdgeBehavior.WRAP,
                        new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y).edgeBehaviorY()),
                () -> assertEquals(EdgeBehavior.BLOCK,
                        new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_BLOCK_Y).edgeBehaviorY())
        );
    }

    @Test
    void testCellVertexCount() {
        assertAll(
                () -> assertEquals(3,
                        new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_XY).cellVertexCount()),
                () -> assertEquals(4, new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY).cellVertexCount()),
                () -> assertEquals(6,
                        new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_XY).cellVertexCount())
        );
    }

    @Test
    void testToDisplayString() {
        assertAll(
                () -> assertEquals("[SQUARE BLOCK/WRAP]",
                        new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y).toDisplayString()),
                () -> assertEquals("[HEXAGON WRAP/BLOCK]",
                        new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_BLOCK_Y).toDisplayString()),
                () -> assertEquals("[SQUARE BLOCK]",
                        new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY).toDisplayString()),
                () -> assertEquals("[HEXAGON WRAP]",
                        new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY).toDisplayString()),
                () -> assertEquals("[TRIANGLE ABSORB]",
                        new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY).toDisplayString())
        );
    }

}
