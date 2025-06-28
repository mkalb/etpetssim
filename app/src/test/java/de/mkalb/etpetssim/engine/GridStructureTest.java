package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
class GridStructureTest {

    @Test
    void testCellShapeAndEdgeBehaviors() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, BoundaryType.WRAP_X_BLOCK_Y),
                new GridSize(20, 30));

        assertEquals(CellShape.SQUARE, structure.cellShape());
        assertEquals(EdgeBehavior.WRAP, structure.edgeBehaviorX());
        assertEquals(EdgeBehavior.BLOCK, structure.edgeBehaviorY());
    }

    @Test
    void testCellCount() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.HEXAGON, BoundaryType.REFLECT_XY),
                new GridSize(20, 30));

        assertEquals(20 * 30, structure.cellCount());
    }

    @Test
    void testCoordinateBounds() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.TRIANGLE, BoundaryType.ABSORB_XY),
                new GridSize(20, 30));

        assertEquals(new GridCoordinate(0, 0), structure.minCoordinateInclusive());
        assertEquals(new GridCoordinate(20, 30), structure.maxCoordinateExclusive());
        assertEquals(new GridCoordinate(20 - 1, 30 - 1), structure.maxCoordinateInclusive());
    }

    @Test
    void testIsCoordinateValid() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, BoundaryType.BLOCK_X_BLOCK_Y),
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
    void testAllCoordinates() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, BoundaryType.BLOCK_X_BLOCK_Y),
                new GridSize(20, 30));

        List<GridCoordinate> coordinates = structure.allCoordinates().toList();
        assertEquals(20 * 30, coordinates.size());
        assertTrue(coordinates.contains(new GridCoordinate(10, 15)));
        assertEquals(new GridCoordinate(0, 0), coordinates.getFirst());
        assertEquals(new GridCoordinate(19, 29), coordinates.getLast());
    }

}
