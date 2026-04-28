package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"MagicNumber", "DuplicateExpressions"})
final class CellNeighborhoodsTest {

    private static void tryPutOuterRadiusRing(SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>>> radiusRings) {
        radiusRings.put(1, new TreeMap<>());
    }

    private static void tryPutInnerRadiusRing(SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>> ring,
                                              GridCoordinate coordinate,
                                              RadiusRingCell<GridCoordinate> ringCell) {
        ring.put(coordinate, ringCell);
    }

    private static void tryAddReachedFromPreviousRing(SortedSet<GridCoordinate> reachedFromPreviousRing,
                                                      GridCoordinate coordinate) {
        reachedFromPreviousRing.add(coordinate);
    }

    @Test
    void testStaticMaxNeighborCount() {
        assertEquals(3, CellNeighborhoods.maxNeighborCount(CellShape.TRIANGLE, NeighborhoodMode.EDGES_ONLY));
        assertEquals(12, CellNeighborhoods.maxNeighborCount(CellShape.TRIANGLE, NeighborhoodMode.EDGES_AND_VERTICES));
        assertEquals(4, CellNeighborhoods.maxNeighborCount(CellShape.SQUARE, NeighborhoodMode.EDGES_ONLY));
        assertEquals(8, CellNeighborhoods.maxNeighborCount(CellShape.SQUARE, NeighborhoodMode.EDGES_AND_VERTICES));
        assertEquals(6, CellNeighborhoods.maxNeighborCount(CellShape.HEXAGON, NeighborhoodMode.EDGES_ONLY));
        assertEquals(6, CellNeighborhoods.maxNeighborCount(CellShape.HEXAGON, NeighborhoodMode.EDGES_AND_VERTICES));
    }

    @Test
    void testStaticMaxNeighborCountRadius() {
        // Radius <= 0 returns 0
        assertEquals(0, CellNeighborhoods.maxNeighborCount(CellShape.TRIANGLE, NeighborhoodMode.EDGES_ONLY, 0));
        assertEquals(0, CellNeighborhoods.maxNeighborCount(CellShape.TRIANGLE, NeighborhoodMode.EDGES_ONLY, -1));
        assertEquals(0, CellNeighborhoods.maxNeighborCount(CellShape.SQUARE, NeighborhoodMode.EDGES_ONLY, 0));
        assertEquals(0, CellNeighborhoods.maxNeighborCount(CellShape.SQUARE, NeighborhoodMode.EDGES_ONLY, -1));
        assertEquals(0, CellNeighborhoods.maxNeighborCount(CellShape.HEXAGON, NeighborhoodMode.EDGES_ONLY, 0));
        assertEquals(0, CellNeighborhoods.maxNeighborCount(CellShape.HEXAGON, NeighborhoodMode.EDGES_ONLY, -1));
        // For radius 1, reuse maxNeighborCount
        assertEquals(CellNeighborhoods.maxNeighborCount(CellShape.TRIANGLE, NeighborhoodMode.EDGES_ONLY),
                CellNeighborhoods.maxNeighborCount(CellShape.TRIANGLE, NeighborhoodMode.EDGES_ONLY, 1));
        assertEquals(CellNeighborhoods.maxNeighborCount(CellShape.SQUARE, NeighborhoodMode.EDGES_ONLY),
                CellNeighborhoods.maxNeighborCount(CellShape.SQUARE, NeighborhoodMode.EDGES_ONLY, 1));
        assertEquals(CellNeighborhoods.maxNeighborCount(CellShape.HEXAGON, NeighborhoodMode.EDGES_ONLY),
                CellNeighborhoods.maxNeighborCount(CellShape.HEXAGON, NeighborhoodMode.EDGES_ONLY, 1));
        // radius 2
        assertEquals(10, CellNeighborhoods.maxNeighborCount(CellShape.TRIANGLE, NeighborhoodMode.EDGES_ONLY, 2));
        assertEquals(37, CellNeighborhoods.maxNeighborCount(CellShape.TRIANGLE, NeighborhoodMode.EDGES_AND_VERTICES, 2));
        assertEquals(13, CellNeighborhoods.maxNeighborCount(CellShape.SQUARE, NeighborhoodMode.EDGES_ONLY, 2));
        assertEquals(25, CellNeighborhoods.maxNeighborCount(CellShape.SQUARE, NeighborhoodMode.EDGES_AND_VERTICES, 2));
        assertEquals(19, CellNeighborhoods.maxNeighborCount(CellShape.HEXAGON, NeighborhoodMode.EDGES_ONLY, 2));
        assertEquals(19, CellNeighborhoods.maxNeighborCount(CellShape.HEXAGON, NeighborhoodMode.EDGES_AND_VERTICES, 2));
    }

    @Test
    void testStaticIsCellNeighbor() {
        GridCoordinate coordinate0 = new GridCoordinate(2, 2);
        GridCoordinate coordinate1 = new GridCoordinate(2, 1);
        GridCoordinate coordinate2 = new GridCoordinate(1, 2);

        // Same coordinate is not a neighbor.
        assertFalse(CellNeighborhoods.isCellNeighbor(coordinate0, coordinate0, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE));
        assertFalse(CellNeighborhoods.isCellNeighbor(coordinate1, coordinate1, NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE));
        assertFalse(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate2, NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));

        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate0, coordinate1, NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate0, coordinate1, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE));
        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate0, coordinate1, NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));

        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate1, coordinate0, NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate1, coordinate0, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE));
        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate1, coordinate0, NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));

        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate0, NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertFalse(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE));

        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate0, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE));
        assertFalse(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE));
        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, NeighborhoodMode.EDGES_AND_VERTICES, CellShape.SQUARE));

        assertTrue(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate0, NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));
        assertFalse(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));
    }

    @Test
    void testStaticEdgeActionForCoordinate() {
        var actionMap = Map.of(
                GridEdgeBehavior.BLOCK_XY, EdgeBehaviorAction.BLOCKED,
                GridEdgeBehavior.ABSORB_XY, EdgeBehaviorAction.ABSORBED,
                GridEdgeBehavior.WRAP_XY, EdgeBehaviorAction.WRAPPED
        );
        for (var entry : actionMap.entrySet()) {
            GridEdgeBehavior edgeBehavior = entry.getKey();
            EdgeBehaviorAction expectedAction = entry.getValue();
            for (CellShape cellShape : CellShape.values()) {
                GridStructure structure = new GridStructure(
                        new GridTopology(cellShape, edgeBehavior),
                        new GridSize(20, 10));

                assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(0, 0), structure));
                assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(19, 0), structure));
                assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(0, 9), structure));
                assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(19, 9), structure));
                assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(5, 5), structure));

                assertEquals(expectedAction, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(-1, -1), structure));
                assertEquals(expectedAction, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(-1, 5), structure));
                assertEquals(expectedAction, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(20, 5), structure));
                assertEquals(expectedAction, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(5, -1), structure));
                assertEquals(expectedAction, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(5, 10), structure));
                assertEquals(expectedAction, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(20, 10), structure));
                assertEquals(expectedAction, CellNeighborhoods.edgeActionForCoordinate(
                        new GridCoordinate(25, 15), structure));
            }
        }

        // BLOCK_X_WRAP_Y
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.BLOCK_X_WRAP_Y),
                    new GridSize(20, 10));

            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(0, 0), structure));
            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(19, 0), structure));
            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(0, 9), structure));
            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(19, 9), structure));
            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(5, 5), structure));

            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(-1, -1), structure));
            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(-1, 5), structure));
            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(20, 5), structure));
            assertEquals(EdgeBehaviorAction.WRAPPED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(5, -1), structure));
            assertEquals(EdgeBehaviorAction.WRAPPED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(5, 10), structure));
            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(20, 10), structure));
            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(25, 15), structure));
        }

        // WRAP_X_BLOCK_Y
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.WRAP_X_BLOCK_Y),
                    new GridSize(20, 10));

            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(0, 0), structure));
            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(19, 0), structure));
            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(0, 9), structure));
            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(19, 9), structure));
            assertEquals(EdgeBehaviorAction.VALID, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(5, 5), structure));

            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(-1, -1), structure));
            assertEquals(EdgeBehaviorAction.WRAPPED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(-1, 5), structure));
            assertEquals(EdgeBehaviorAction.WRAPPED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(20, 5), structure));
            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(5, -1), structure));
            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(5, 10), structure));
            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(20, 10), structure));
            assertEquals(EdgeBehaviorAction.BLOCKED, CellNeighborhoods.edgeActionForCoordinate(
                    new GridCoordinate(25, 15), structure));
        }
    }

    @Test
    void testStaticIsValidEdgeCoordinate() {
        // BLOCK_XY, ABSORB_XY
        var invalid = Set.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.ABSORB_XY);
        for (GridEdgeBehavior gridEdgeBehavior : invalid) {
            for (CellShape cellShape : CellShape.values()) {
                GridStructure structure = new GridStructure(
                        new GridTopology(cellShape, gridEdgeBehavior),
                        new GridSize(20, 10));
                assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(0, 0), structure));
                assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(5, 5), structure));
                assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(19, 9), structure));
                assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(-1, -1), structure));
                assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(-1, 5), structure));
                assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(5, -1), structure));
                assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(20, 10), structure));
                assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(20, 9), structure));
                assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(19, 10), structure));
            }
        }
        // WRAP_XY
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.WRAP_XY),
                    new GridSize(20, 10));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(0, 0), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(5, 5), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(19, 9), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(-1, -1), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(-1, 5), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(5, -1), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(20, 10), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(20, 9), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(19, 10), structure));
        }
        // WRAP_X_BLOCK_Y
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.WRAP_X_BLOCK_Y),
                    new GridSize(20, 10));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(0, 0), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(5, 5), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(19, 9), structure));
            assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(-1, -1), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(-1, 5), structure));
            assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(5, -1), structure));
            assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(20, 10), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(20, 9), structure));
            assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(19, 10), structure));
        }
        // BLOCK_X_WRAP_Y
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.BLOCK_X_WRAP_Y),
                    new GridSize(20, 10));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(0, 0), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(5, 5), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(19, 9), structure));
            assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(-1, -1), structure));
            assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(-1, 5), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(5, -1), structure));
            assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(20, 10), structure));
            assertFalse(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(20, 9), structure));
            assertTrue(CellNeighborhoods.isValidEdgeCoordinate(new GridCoordinate(19, 10), structure));
        }
    }

    @Test
    void testStaticApplyEdgeBehaviorToCoordinate() {
        // All VALID (inside grid)
        for (GridEdgeBehavior gridEdgeBehavior : GridEdgeBehavior.values()) {
            for (CellShape cellShape : CellShape.values()) {
                GridStructure structure = new GridStructure(
                        new GridTopology(cellShape, gridEdgeBehavior),
                        new GridSize(20, 10));

                assertEquals(new EdgeBehaviorResult(new GridCoordinate(0, 0), new GridCoordinate(0, 0), EdgeBehaviorAction.VALID),
                        CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(0, 0), structure));
                assertEquals(new EdgeBehaviorResult(new GridCoordinate(5, 5), new GridCoordinate(5, 5), EdgeBehaviorAction.VALID),
                        CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(5, 5), structure));
                assertEquals(new EdgeBehaviorResult(new GridCoordinate(19, 9), new GridCoordinate(19, 9), EdgeBehaviorAction.VALID),
                        CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(19, 9), structure));
            }
        }
        // ABSORB_XY
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.ABSORB_XY),
                    new GridSize(20, 10));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-1, -1), new GridCoordinate(-1, -1), EdgeBehaviorAction.ABSORBED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-1, -1), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, -2), new GridCoordinate(-3, -2), EdgeBehaviorAction.ABSORBED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(0, -2), new GridCoordinate(0, -2), EdgeBehaviorAction.ABSORBED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(0, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, 1), new GridCoordinate(-3, 1), EdgeBehaviorAction.ABSORBED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, 1), structure));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(20, 5), new GridCoordinate(20, 5), EdgeBehaviorAction.ABSORBED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(20, 5), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(5, 15), new GridCoordinate(5, 15), EdgeBehaviorAction.ABSORBED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(5, 15), structure));
        }
        // BLOCK_XY
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.BLOCK_XY),
                    new GridSize(20, 10));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-1, -1), new GridCoordinate(-1, -1), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-1, -1), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, -2), new GridCoordinate(-3, -2), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(0, -2), new GridCoordinate(0, -2), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(0, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, 1), new GridCoordinate(-3, 1), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, 1), structure));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(20, 5), new GridCoordinate(20, 5), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(20, 5), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(5, 15), new GridCoordinate(5, 15), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(5, 15), structure));
        }
        // WRAP_X_BLOCK_Y
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.WRAP_X_BLOCK_Y),
                    new GridSize(20, 10));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-1, -1), new GridCoordinate(-1, -1), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-1, -1), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, -2), new GridCoordinate(-3, -2), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(0, -2), new GridCoordinate(0, -2), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(0, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, 1), new GridCoordinate(17, 1), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, 1), structure));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(20, 5), new GridCoordinate(0, 5), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(20, 5), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(5, 15), new GridCoordinate(5, 15), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(5, 15), structure));
        }
        // BLOCK_X_WRAP_Y
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.BLOCK_X_WRAP_Y),
                    new GridSize(20, 10));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-1, -1), new GridCoordinate(-1, -1), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-1, -1), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, -2), new GridCoordinate(-3, -2), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(0, -2), new GridCoordinate(0, 8), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(0, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, 1), new GridCoordinate(-3, 1), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, 1), structure));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(20, 5), new GridCoordinate(20, 5), EdgeBehaviorAction.BLOCKED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(20, 5), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(5, 15), new GridCoordinate(5, 5), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(5, 15), structure));
        }
        // WRAP_XY
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = new GridStructure(
                    new GridTopology(cellShape, GridEdgeBehavior.WRAP_XY),
                    new GridSize(20, 10));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-1, -1), new GridCoordinate(19, 9), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-1, -1), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, -2), new GridCoordinate(17, 8), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(0, -2), new GridCoordinate(0, 8), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(0, -2), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(-3, 1), new GridCoordinate(17, 1), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(-3, 1), structure));

            assertEquals(new EdgeBehaviorResult(new GridCoordinate(20, 5), new GridCoordinate(0, 5), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(20, 5), structure));
            assertEquals(new EdgeBehaviorResult(new GridCoordinate(5, 15), new GridCoordinate(5, 5), EdgeBehaviorAction.WRAPPED),
                    CellNeighborhoods.applyEdgeBehaviorToCoordinate(new GridCoordinate(5, 15), structure));
        }
    }

    @Test
    void testStaticCellsByRadiusRingsRadius0AndUnmodifiable() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                GridSize.EXTRA_SMALL_SQUARE);
        GridCoordinate startCoordinate = new GridCoordinate(1, 1);

        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>>> radiusRings =
                CellNeighborhoods.cellsByRadiusRings(
                        startCoordinate,
                        NeighborhoodMode.EDGES_ONLY,
                        structure,
                        0,
                        Function.identity());

        assertEquals(Set.of(0), radiusRings.keySet());
        assertEquals(Set.of(startCoordinate), radiusRings.get(0).keySet());

        RadiusRingCell<GridCoordinate> ringCell = radiusRings.get(0).get(startCoordinate);
        assertEquals(0, ringCell.ring());
        assertEquals(startCoordinate, ringCell.coordinate());
        assertEquals(startCoordinate, ringCell.cell());
        assertTrue(ringCell.reachedFromPreviousRing().isEmpty());

        assertThrows(UnsupportedOperationException.class, () -> tryPutOuterRadiusRing(radiusRings));
        assertThrows(UnsupportedOperationException.class, () -> tryPutInnerRadiusRing(radiusRings.get(0), startCoordinate, ringCell));
        assertThrows(UnsupportedOperationException.class, () -> tryAddReachedFromPreviousRing(ringCell.reachedFromPreviousRing(), startCoordinate));
    }

    @Test
    void testStaticCellsByRadiusRingsInvalidRadiusOrInvalidCoordinate() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                GridSize.EXTRA_SMALL_SQUARE);

        assertThrows(IllegalArgumentException.class, () -> CellNeighborhoods.cellsByRadiusRings(
                new GridCoordinate(1, 1),
                NeighborhoodMode.EDGES_ONLY,
                structure,
                -1,
                Function.identity()));
        assertThrows(IllegalArgumentException.class, () -> CellNeighborhoods.cellsByRadiusRings(
                new GridCoordinate(1, 1),
                NeighborhoodMode.EDGES_ONLY,
                structure,
                CellNeighborhoods.MAX_RADIUS + 1,
                Function.identity()));
        assertThrows(IllegalArgumentException.class, () -> CellNeighborhoods.cellsByRadiusRings(
                new GridCoordinate(8, 1),
                NeighborhoodMode.EDGES_ONLY,
                structure,
                1,
                Function.identity()));
    }

    @Test
    void testStaticCellsByRadiusRingsWrapBehaviorAndUniqueness() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY),
                GridSize.EXTRA_SMALL_SQUARE);
        GridCoordinate startCoordinate = new GridCoordinate(0, 0);

        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>>> radiusRings =
                CellNeighborhoods.cellsByRadiusRings(
                        startCoordinate,
                        NeighborhoodMode.EDGES_ONLY,
                        structure,
                        2,
                        Function.identity());

        assertEquals(Set.of(0, 1, 2), radiusRings.keySet());
        assertEquals(Set.of(startCoordinate), radiusRings.get(0).keySet());
        assertEquals(Set.of(
                        new GridCoordinate(0, 7),
                        new GridCoordinate(1, 0),
                        new GridCoordinate(0, 1),
                        new GridCoordinate(7, 0)),
                radiusRings.get(1).keySet());
        assertEquals(Set.of(
                        new GridCoordinate(0, 6),
                        new GridCoordinate(1, 7),
                        new GridCoordinate(7, 7),
                        new GridCoordinate(2, 0),
                        new GridCoordinate(1, 1),
                        new GridCoordinate(0, 2),
                        new GridCoordinate(7, 1),
                        new GridCoordinate(6, 0)),
                radiusRings.get(2).keySet());

        for (Map.Entry<GridCoordinate, RadiusRingCell<GridCoordinate>> entry : radiusRings.get(1).entrySet()) {
            assertEquals(1, entry.getValue().ring());
            assertEquals(entry.getKey(), entry.getValue().coordinate());
            assertEquals(entry.getKey(), entry.getValue().cell());
            assertEquals(Set.of(startCoordinate), entry.getValue().reachedFromPreviousRing());
        }

        assertEquals(Set.of(new GridCoordinate(0, 7)),
                radiusRings.get(2).get(new GridCoordinate(0, 6)).reachedFromPreviousRing());
        assertEquals(Set.of(new GridCoordinate(0, 7), new GridCoordinate(1, 0)),
                radiusRings.get(2).get(new GridCoordinate(1, 7)).reachedFromPreviousRing());
        assertEquals(Set.of(new GridCoordinate(0, 7), new GridCoordinate(7, 0)),
                radiusRings.get(2).get(new GridCoordinate(7, 7)).reachedFromPreviousRing());
        assertEquals(Set.of(new GridCoordinate(1, 0)),
                radiusRings.get(2).get(new GridCoordinate(2, 0)).reachedFromPreviousRing());
        assertEquals(Set.of(new GridCoordinate(1, 0), new GridCoordinate(0, 1)),
                radiusRings.get(2).get(new GridCoordinate(1, 1)).reachedFromPreviousRing());
        assertEquals(Set.of(new GridCoordinate(0, 1)),
                radiusRings.get(2).get(new GridCoordinate(0, 2)).reachedFromPreviousRing());
        assertEquals(Set.of(new GridCoordinate(0, 1), new GridCoordinate(7, 0)),
                radiusRings.get(2).get(new GridCoordinate(7, 1)).reachedFromPreviousRing());
        assertEquals(Set.of(new GridCoordinate(7, 0)),
                radiusRings.get(2).get(new GridCoordinate(6, 0)).reachedFromPreviousRing());

        List<GridCoordinate> allCoordinates = new ArrayList<>();
        for (SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>> ring : radiusRings.values()) {
            allCoordinates.addAll(ring.keySet());
        }
        assertEquals(13, allCoordinates.size());
        assertEquals(13, new HashSet<>(allCoordinates).size());
    }

    @Test
    void testStaticCellsByRadiusRingsFiltersBlockedAndAbsorbedNeighbors() {
        GridCoordinate startCoordinate = new GridCoordinate(0, 0);

        for (GridEdgeBehavior edgeBehavior : List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.ABSORB_XY)) {
            GridStructure structure = new GridStructure(
                    new GridTopology(CellShape.SQUARE, edgeBehavior),
                    GridSize.EXTRA_SMALL_SQUARE);

            SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>>> radiusRings =
                    CellNeighborhoods.cellsByRadiusRings(
                            startCoordinate,
                            NeighborhoodMode.EDGES_ONLY,
                            structure,
                            1,
                            Function.identity());

            assertEquals(Set.of(
                            new GridCoordinate(1, 0),
                            new GridCoordinate(0, 1)),
                    radiusRings.get(1).keySet());
            assertFalse(radiusRings.get(1).containsKey(new GridCoordinate(-1, 0)));
            assertFalse(radiusRings.get(1).containsKey(new GridCoordinate(0, -1)));
        }
    }

    @Test
    void testRadiusRingCellEqualsAndHashCodeUseCoordinateOnly() {
        GridCoordinate coordinate = new GridCoordinate(1, 1);

        RadiusRingCell<String> cellA = new RadiusRingCell<>(0, coordinate, "A", new TreeSet<>());
        RadiusRingCell<String> cellB = new RadiusRingCell<>(2, coordinate, "B",
                new TreeSet<>(Set.of(new GridCoordinate(1, 0))));
        RadiusRingCell<String> cellC = new RadiusRingCell<>(2, new GridCoordinate(1, 2), "B", new TreeSet<>());

        assertEquals(cellA, cellB);
        assertEquals(cellA.hashCode(), cellB.hashCode());
        assertNotEquals(cellA, cellC);
    }

    @SuppressWarnings("EmptyMethod")
    void testStaticNeighborEdgeResults() {
        // TODO implement test method
    }

    @SuppressWarnings("EmptyMethod")
    void testStaticCellNeighborsIgnoringEdgeBehavior() {
        // TODO implement test method
    }

    @SuppressWarnings("EmptyMethod")
    void testStaticCellNeighborWithEdgeBehavior() {
        // TODO implement test method
    }

    @SuppressWarnings("EmptyMethod")
    void testStaticCellNeighborsWithEdgeBehavior() {
        // TODO implement test method
    }

    @SuppressWarnings("EmptyMethod")
    void testStaticCoordinatesOfNeighbors() {
        // TODO implement test method
    }

    @Test
    void testStaticGetCellNeighborConnections() {
        // getCellNeighborConnections returns the cached list of computeCellNeighborConnections
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                GridCoordinate coordinate = new GridCoordinate(x, y);
                for (NeighborhoodMode neighborhoodMode : NeighborhoodMode.values()) {
                    for (CellShape cellShape : CellShape.values()) {
                        assertEquals(CellNeighborhoods.computeCellNeighborConnections(coordinate, neighborhoodMode, cellShape),
                                CellNeighborhoods.getCellNeighborConnections(coordinate, neighborhoodMode, cellShape));
                    }
                }
            }
        }
    }

    @Test
    void testStaticGenerateCacheKey() {
        assertEquals("SQUARE::EDGES_ONLY", CellNeighborhoods.generateCacheKey(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE));
        assertEquals("SQUARE::EDGES_AND_VERTICES", CellNeighborhoods.generateCacheKey(new GridCoordinate(3, 5), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.SQUARE));

        assertEquals("HEXAGON::false", CellNeighborhoods.generateCacheKey(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));
        assertEquals("HEXAGON::false", CellNeighborhoods.generateCacheKey(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.HEXAGON));
        assertEquals("HEXAGON::true", CellNeighborhoods.generateCacheKey(new GridCoordinate(3, 2), NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));
        assertEquals("HEXAGON::true", CellNeighborhoods.generateCacheKey(new GridCoordinate(3, 2), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.HEXAGON));
        assertEquals("HEXAGON::false", CellNeighborhoods.generateCacheKey(new GridCoordinate(2, 5), NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));
        assertEquals("HEXAGON::false", CellNeighborhoods.generateCacheKey(new GridCoordinate(2, 5), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.HEXAGON));
        assertEquals("HEXAGON::true", CellNeighborhoods.generateCacheKey(new GridCoordinate(3, 5), NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));
        assertEquals("HEXAGON::true", CellNeighborhoods.generateCacheKey(new GridCoordinate(3, 5), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.HEXAGON));

        assertEquals("TRIANGLE::EDGES_ONLY::true", CellNeighborhoods.generateCacheKey(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertEquals("TRIANGLE::EDGES_AND_VERTICES::true", CellNeighborhoods.generateCacheKey(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE));
        assertEquals("TRIANGLE::EDGES_ONLY::true", CellNeighborhoods.generateCacheKey(new GridCoordinate(3, 5), NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertEquals("TRIANGLE::EDGES_AND_VERTICES::true", CellNeighborhoods.generateCacheKey(new GridCoordinate(3, 5), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE));

        assertEquals("TRIANGLE::EDGES_ONLY::false", CellNeighborhoods.generateCacheKey(new GridCoordinate(2, 3), NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertEquals("TRIANGLE::EDGES_AND_VERTICES::false", CellNeighborhoods.generateCacheKey(new GridCoordinate(2, 3), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE));
        assertEquals("TRIANGLE::EDGES_ONLY::false", CellNeighborhoods.generateCacheKey(new GridCoordinate(3, 4), NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertEquals("TRIANGLE::EDGES_AND_VERTICES::false", CellNeighborhoods.generateCacheKey(new GridCoordinate(3, 4), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE));

    }

    @Test
    void testStaticComputeCellNeighborConnections() {
        assertEquals(CellNeighborhoods.computeTriangleCellNeighborConnections(NeighborhoodMode.EDGES_ONLY, true),
                CellNeighborhoods.computeCellNeighborConnections(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertEquals(CellNeighborhoods.computeTriangleCellNeighborConnections(NeighborhoodMode.EDGES_AND_VERTICES, true),
                CellNeighborhoods.computeCellNeighborConnections(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE));
        assertEquals(CellNeighborhoods.computeTriangleCellNeighborConnections(NeighborhoodMode.EDGES_ONLY, false),
                CellNeighborhoods.computeCellNeighborConnections(new GridCoordinate(3, 2), NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE));
        assertEquals(CellNeighborhoods.computeTriangleCellNeighborConnections(NeighborhoodMode.EDGES_AND_VERTICES, false),
                CellNeighborhoods.computeCellNeighborConnections(new GridCoordinate(3, 2), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE));

        assertEquals(CellNeighborhoods.computeSquareCellNeighborConnections(NeighborhoodMode.EDGES_ONLY),
                CellNeighborhoods.computeCellNeighborConnections(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE));
        assertEquals(CellNeighborhoods.computeSquareCellNeighborConnections(NeighborhoodMode.EDGES_AND_VERTICES),
                CellNeighborhoods.computeCellNeighborConnections(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_AND_VERTICES, CellShape.SQUARE));

        assertEquals(CellNeighborhoods.computeHexagonCellNeighborConnections(false),
                CellNeighborhoods.computeCellNeighborConnections(new GridCoordinate(2, 2), NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));
        assertEquals(CellNeighborhoods.computeHexagonCellNeighborConnections(true),
                CellNeighborhoods.computeCellNeighborConnections(new GridCoordinate(3, 2), NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON));
    }

    @SuppressWarnings("EmptyMethod")
    void testStaticComputeTriangleCellNeighborConnections() {
        // TODO implement test method
    }

    @Test
    void testStaticComputeSquareCellNeighborConnections() {
        // EDGES_ONLY
        {
            var connections = CellNeighborhoods.computeSquareCellNeighborConnections(NeighborhoodMode.EDGES_ONLY);
            assertEquals(4, connections.size());
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(1, 0), CompassDirection.E, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.W, CellConnectionType.EDGE)));
        }
        // EDGES_AND_VERTICES
        {
            var connections = CellNeighborhoods.computeSquareCellNeighborConnections(NeighborhoodMode.EDGES_AND_VERTICES);
            assertEquals(8, connections.size());
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(1, -1), CompassDirection.NE, CellConnectionType.VERTEX)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(1, 0), CompassDirection.E, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(1, 1), CompassDirection.SE, CellConnectionType.VERTEX)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.SW, CellConnectionType.VERTEX)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.W, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.NW, CellConnectionType.VERTEX)));
        }
    }

    @Test
    void testStaticComputeHexagonCellNeighborConnections() {
        // hasHexagonCellYOffset==true
        {
            var connections = CellNeighborhoods.computeHexagonCellNeighborConnections(true);
            assertEquals(6, connections.size());
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(1, 0), CompassDirection.NE, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(1, 1), CompassDirection.SE, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.SW, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.NW, CellConnectionType.EDGE)));
        }
        // hasHexagonCellYOffset==false
        {
            var connections = CellNeighborhoods.computeHexagonCellNeighborConnections(false);
            assertEquals(6, connections.size());
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(1, -1), CompassDirection.NE, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(1, 0), CompassDirection.SE, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.SW, CellConnectionType.EDGE)));
            assertTrue(connections.contains(new CellNeighborhoods.CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.NW, CellConnectionType.EDGE)));
        }
    }

}
