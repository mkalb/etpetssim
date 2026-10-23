package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class CellNeighborTest {

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
        CellNeighbor edgeNeighbor = new CellNeighbor(
                new GridCoordinate(2, 3), CompassDirection.N, CellConnectionType.EDGE, new GridCoordinate(2, 2));
        CellNeighbor vertexNeighbor = new CellNeighbor(
                new GridCoordinate(2, 3), CompassDirection.SE, CellConnectionType.VERTEX, new GridCoordinate(3, 4));

        assertAll(
                () -> assertEquals("(2, 3) ↑ (2, 2) (edge)", edgeNeighbor.toDisplayString()),
                () -> assertEquals("(2, 3) ↘ (3, 4) (vertex)", vertexNeighbor.toDisplayString())
        );
    }

}
