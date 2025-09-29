package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class CellNeighborWithEdgeBehaviorTest {

    @Test
    void testRecord() {
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate orig = new GridCoordinate(1, 0);
        GridCoordinate mapped = new GridCoordinate(1, 0);
        CellNeighbor neighbor = new CellNeighbor(start, CompassDirection.E, CellConnectionType.EDGE, orig);
        EdgeBehaviorResult edgeResult = new EdgeBehaviorResult(orig, mapped, EdgeBehaviorAction.VALID);

        CellNeighborWithEdgeBehavior n1 = CellNeighborWithEdgeBehavior.of(neighbor, edgeResult);
        CellNeighborWithEdgeBehavior n2 = new CellNeighborWithEdgeBehavior(
                start, CompassDirection.E, CellConnectionType.EDGE, orig, mapped, EdgeBehaviorAction.VALID);

        assertEquals(n1, n2);
        assertEquals(start, n1.startCoordinate());
        assertEquals(orig, n1.originalNeighborCoordinate());
        assertEquals(mapped, n1.mappedNeighborCoordinate());
        assertEquals(EdgeBehaviorAction.VALID, n1.edgeBehaviorAction());
    }

    @Test
    void testStaticOf() {
        CellNeighborWithEdgeBehavior cellNeighborWithEdgeBehavior = CellNeighborWithEdgeBehavior.of(
                new CellNeighbor(new GridCoordinate(1, 1), CompassDirection.W, CellConnectionType.VERTEX, new GridCoordinate(0, 1)),
                new EdgeBehaviorResult(new GridCoordinate(0, 1), new GridCoordinate(0, 1), EdgeBehaviorAction.VALID)
        );
        assertEquals(new GridCoordinate(1, 1), cellNeighborWithEdgeBehavior.startCoordinate());
        assertEquals(CompassDirection.W, cellNeighborWithEdgeBehavior.direction());
        assertEquals(CellConnectionType.VERTEX, cellNeighborWithEdgeBehavior.connection());
        assertEquals(new GridCoordinate(0, 1), cellNeighborWithEdgeBehavior.originalNeighborCoordinate());
        assertEquals(new GridCoordinate(0, 1), cellNeighborWithEdgeBehavior.mappedNeighborCoordinate());
        assertEquals(EdgeBehaviorAction.VALID, cellNeighborWithEdgeBehavior.edgeBehaviorAction());
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
        GridCoordinate start = new GridCoordinate(2, 3);
        GridCoordinate orig = new GridCoordinate(2, 4);
        GridCoordinate mapped = new GridCoordinate(2, 4);
        CellNeighborWithEdgeBehavior n = new CellNeighborWithEdgeBehavior(
                start, CompassDirection.N, CellConnectionType.EDGE, orig, mapped, EdgeBehaviorAction.VALID);
        String display = n.toDisplayString();
        assertTrue(display.contains(start.toDisplayString()));
        assertTrue(display.contains(orig.toDisplayString()));
        assertTrue(display.contains(mapped.toDisplayString()));
        assertTrue(display.contains("edge"));
        assertTrue(display.contains("valid"));
    }

}
