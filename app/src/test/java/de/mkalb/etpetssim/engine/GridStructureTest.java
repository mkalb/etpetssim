package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
class GridStructureTest {

    @Test
    void testCellShapeAndEdgeBehaviors() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_X_BLOCK_Y),
                new GridSize(20, 30));

        assertEquals(CellShape.SQUARE, structure.cellShape());
        assertEquals(EdgeBehavior.WRAP, structure.edgeBehaviorX());
        assertEquals(EdgeBehavior.BLOCK, structure.edgeBehaviorY());
    }

    @Test
    void testCellCount() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.REFLECT_XY),
                new GridSize(20, 30));

        assertEquals(20 * 30, structure.cellCount());
    }

    @Test
    void testCoordinateBounds() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY),
                new GridSize(20, 30));

        assertEquals(new GridCoordinate(0, 0), structure.minCoordinateInclusive());
        assertEquals(new GridCoordinate(20, 30), structure.maxCoordinateExclusive());
        assertEquals(new GridCoordinate(20 - 1, 30 - 1), structure.maxCoordinateInclusive());
    }

    @Test
    void testIsCoordinateValid() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(20, 30));

        assertTrue(structure.isCoordinateValid(new GridCoordinate(0, 0)));
        assertTrue(structure.isCoordinateValid(new GridCoordinate(0, 29)));
        assertTrue(structure.isCoordinateValid(new GridCoordinate(19, 29)));
        assertTrue(structure.isCoordinateValid(new GridCoordinate(19, 0)));
        assertTrue(structure.isCoordinateValid(new GridCoordinate(10, 15)));

        assertFalse(structure.isCoordinateValid(new GridCoordinate(-1, -1)));
        assertFalse(structure.isCoordinateValid(new GridCoordinate(-1, 0)));
        assertFalse(structure.isCoordinateValid(new GridCoordinate(0, -1)));

        assertFalse(structure.isCoordinateValid(new GridCoordinate(20, 0)));
        assertFalse(structure.isCoordinateValid(new GridCoordinate(0, 30)));
        assertFalse(structure.isCoordinateValid(new GridCoordinate(20, 30)));
    }

    @Test
    void testCoordinatesStream() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(20, 30));

        List<GridCoordinate> coordinates = structure.coordinatesStream().toList();
        assertEquals(20 * 30, coordinates.size());
        assertTrue(coordinates.contains(new GridCoordinate(10, 15)));
        assertEquals(new GridCoordinate(0, 0), coordinates.getFirst());
        assertEquals(new GridCoordinate(19, 29), coordinates.getLast());

        // Check that all coordinates are valid and unique
        Set<GridCoordinate> unique = new HashSet<>(coordinates);
        assertEquals(coordinates.size(), unique.size());
        for (GridCoordinate c : coordinates) {
            assertTrue(structure.isCoordinateValid(c));
        }
    }

    @Test
    void testCoordinatesList() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(20, 30));

        List<GridCoordinate> coordinates = structure.coordinatesList();
        assertEquals(20 * 30, coordinates.size());
        assertTrue(coordinates.contains(new GridCoordinate(10, 15)));
        assertEquals(new GridCoordinate(0, 0), coordinates.getFirst());
        assertEquals(new GridCoordinate(19, 29), coordinates.getLast());

        // Check that all coordinates are valid and unique
        Set<GridCoordinate> unique = new HashSet<>(coordinates);
        assertEquals(coordinates.size(), unique.size());
        for (GridCoordinate c : coordinates) {
            assertTrue(structure.isCoordinateValid(c));
        }
    }

    @Test
    void testToDisplayString() {
        assertEquals("[HEXAGON WRAP] 20 × 30", new GridStructure(
                new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_WRAP_Y),
                new GridSize(20, 30)).toDisplayString());
        assertEquals("[TRIANGLE BLOCK] 10 × 16", new GridStructure(
                new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(10, 16)).toDisplayString());
        assertEquals("[SQUARE BLOCK/WRAP] 32 × 32", new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y),
                new GridSize(32, 32)).toDisplayString());
    }

}
