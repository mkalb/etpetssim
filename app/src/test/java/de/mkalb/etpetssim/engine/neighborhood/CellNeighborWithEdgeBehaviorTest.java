package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CellNeighborWithEdgeBehaviorTest {

    @Test
    void testOf() {
        CellNeighborWithEdgeBehavior cellNeighborWithEdgeBehavior = CellNeighborWithEdgeBehavior.of(
                new CellNeighbor(new GridCoordinate(0, 1), CompassDirection.W, CellConnectionType.VERTEX, new GridCoordinate(-1, 1)),
                new EdgeBehaviorResult(new GridCoordinate(-1, 1), new GridCoordinate(9, 1), EdgeBehaviorAction.WRAPPED)
        );
        assertAll(
                () -> assertEquals(new GridCoordinate(0, 1), cellNeighborWithEdgeBehavior.startCoordinate()),
                () -> assertEquals(CompassDirection.W, cellNeighborWithEdgeBehavior.direction()),
                () -> assertEquals(CellConnectionType.VERTEX, cellNeighborWithEdgeBehavior.connection()),
                () -> assertEquals(new GridCoordinate(-1, 1), cellNeighborWithEdgeBehavior.originalNeighborCoordinate()),
                () -> assertEquals(new GridCoordinate(9, 1), cellNeighborWithEdgeBehavior.mappedNeighborCoordinate()),
                () -> assertEquals(EdgeBehaviorAction.WRAPPED, cellNeighborWithEdgeBehavior.edgeBehaviorAction())
        );
    }

    @Test
    void testIsEdgeConnection() {
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate orig = new GridCoordinate(0, 1);
        GridCoordinate mapped = new GridCoordinate(0, 1);

        CellNeighborWithEdgeBehavior edgeNeighbor = new CellNeighborWithEdgeBehavior(
                start, CompassDirection.S, CellConnectionType.EDGE, orig, mapped, EdgeBehaviorAction.VALID);
        CellNeighborWithEdgeBehavior vertexNeighbor = new CellNeighborWithEdgeBehavior(
                start, CompassDirection.SE, CellConnectionType.VERTEX, orig, mapped, EdgeBehaviorAction.BLOCKED);

        assertTrue(edgeNeighbor.isEdgeConnection());
        assertFalse(vertexNeighbor.isEdgeConnection());
    }

    @Test
    void testIsVertexConnection() {
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate orig = new GridCoordinate(0, 1);
        GridCoordinate mapped = new GridCoordinate(0, 1);

        CellNeighborWithEdgeBehavior edgeNeighbor = new CellNeighborWithEdgeBehavior(
                start, CompassDirection.S, CellConnectionType.EDGE, orig, mapped, EdgeBehaviorAction.VALID);
        CellNeighborWithEdgeBehavior vertexNeighbor = new CellNeighborWithEdgeBehavior(
                start, CompassDirection.SE, CellConnectionType.VERTEX, orig, mapped, EdgeBehaviorAction.BLOCKED);

        assertFalse(edgeNeighbor.isVertexConnection());
        assertTrue(vertexNeighbor.isVertexConnection());
    }

    @Test
    void testToDisplayString() {
        CellNeighborWithEdgeBehavior validNeighbor = new CellNeighborWithEdgeBehavior(new GridCoordinate(2, 3),
                CompassDirection.N, CellConnectionType.EDGE, new GridCoordinate(2, 2), new GridCoordinate(2, 2),
                EdgeBehaviorAction.VALID);
        CellNeighborWithEdgeBehavior wrappedNeighbor = new CellNeighborWithEdgeBehavior(new GridCoordinate(0, 0),
                CompassDirection.N, CellConnectionType.EDGE, new GridCoordinate(0, -1), new GridCoordinate(0, 9),
                EdgeBehaviorAction.WRAPPED);

        assertAll(
                () -> assertEquals("(2, 3) ↑ (2, 2) → (2, 2) (EDGE, VALID)", validNeighbor.toDisplayString()),
                () -> assertEquals("(0, 0) ↑ (0, -1) → (0, 9) (EDGE, WRAPPED)", wrappedNeighbor.toDisplayString())
        );
    }

}
