package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class CellNeighborTest {

    @Test
    void testRecord() {
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate neighbor = new GridCoordinate(1, 0);
        CellNeighbor n1 = new CellNeighbor(start, CompassDirection.E, CellConnectionType.EDGE, neighbor);
        CellNeighbor n2 = new CellNeighbor(start, CompassDirection.E, CellConnectionType.EDGE, neighbor);

        assertEquals(start, n1.startCoordinate());
        assertEquals(CompassDirection.E, n1.direction());
        assertEquals(CellConnectionType.EDGE, n1.connection());
        assertEquals(neighbor, n1.neighborCoordinate());
        assertEquals(n1, n2);
    }

    @Test
    void testIsEdgeConnection() {
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate neighbor = new GridCoordinate(0, 1);
        CellNeighbor edgeNeighbor = new CellNeighbor(start, CompassDirection.S, CellConnectionType.EDGE, neighbor);
        CellNeighbor vertexNeighbor = new CellNeighbor(start, CompassDirection.SE, CellConnectionType.VERTEX, neighbor);

        assertTrue(edgeNeighbor.isEdgeConnection());
        assertFalse(vertexNeighbor.isEdgeConnection());
    }

    @Test
    void testIsVertexConnection() {
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate neighbor = new GridCoordinate(0, 1);
        CellNeighbor edgeNeighbor = new CellNeighbor(start, CompassDirection.S, CellConnectionType.EDGE, neighbor);
        CellNeighbor vertexNeighbor = new CellNeighbor(start, CompassDirection.SE, CellConnectionType.VERTEX, neighbor);

        assertFalse(edgeNeighbor.isVertexConnection());
        assertTrue(vertexNeighbor.isVertexConnection());
    }

    @Test
    void testToDisplayString() {
        GridCoordinate start = new GridCoordinate(2, 3);
        GridCoordinate neighbor = new GridCoordinate(2, 4);
        CellNeighbor n = new CellNeighbor(start, CompassDirection.N, CellConnectionType.EDGE, neighbor);
        String display = n.toDisplayString();
        assertTrue(display.contains(start.toDisplayString()));
        assertTrue(display.contains(neighbor.toDisplayString()));
        assertTrue(display.contains("edge"));
    }

}
