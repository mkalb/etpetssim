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
        assertEquals(20 * 30, new GridStructure(new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.REFLECT_XY), new GridSize(20, 30)).cellCount());
        assertEquals(30 * 20, new GridStructure(new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.REFLECT_XY), new GridSize(30, 20)).cellCount());
        assertEquals(12 * 16, new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY), new GridSize(12, 16)).cellCount());
    }

    @Test
    void testCoordinateBounds() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY),
                new GridSize(20, 30));

        assertEquals(new GridCoordinate(0, 0), structure.minCoordinateInclusive());
        assertEquals(new GridCoordinate(20, 30), structure.maxCoordinateExclusive());
        assertEquals(new GridCoordinate(19, 29), structure.maxCoordinateInclusive());
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
        assertEquals("[TRIANGLE BLOCK] 12 × 16", new GridStructure(
                new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(12, 16)).toDisplayString());
        assertEquals("[SQUARE BLOCK/WRAP] 32 × 32", new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y),
                new GridSize(32, 32)).toDisplayString());
    }

    @Test
    void testValidMultiplesForAllCellShapes() {
        // BLOCK: any size is valid
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(9, 11)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(9, 11)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(9, 11)));

        // SQUARE: any size is valid (WRAP)
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_X_WRAP_Y),
                new GridSize(8, 8)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_X_WRAP_Y),
                new GridSize(9, 11)));

        // TRIANGLE: height must be multiple of 4 (WRAP)
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_WRAP_Y),
                new GridSize(8, 8)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_WRAP_Y),
                new GridSize(9, 12)));

        // HEXAGON: width must be multiple of 2 (WRAP)
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_WRAP_Y),
                new GridSize(8, 8)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_WRAP_Y),
                new GridSize(10, 9)));
    }

    @Test
    void testIsValidStaticMethod() {
        // BLOCK: any size is valid
        assertTrue(GridStructure.isValid(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_X_BLOCK_Y), new GridSize(9, 11)));
        assertTrue(GridStructure.isValid(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y), new GridSize(9, 11)));
        assertTrue(GridStructure.isValid(new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_X_BLOCK_Y), new GridSize(9, 11)));

        // SQUARE: any size is valid (WRAP)
        GridTopology squareTopology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_X_WRAP_Y);
        assertTrue(GridStructure.isValid(squareTopology, new GridSize(8, 8)));
        assertTrue(GridStructure.isValid(squareTopology, new GridSize(9, 11)));

        // TRIANGLE: height must be multiple of 4 (WRAP)
        GridTopology triTopology = new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_WRAP_Y);
        assertTrue(GridStructure.isValid(triTopology, new GridSize(8, 8)));
        assertTrue(GridStructure.isValid(triTopology, new GridSize(9, 12)));
        assertFalse(GridStructure.isValid(triTopology, new GridSize(8, 10))); // 10 not multiple of 4

        // HEXAGON: width must be multiple of 2 (WRAP)
        GridTopology hexTopology = new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_WRAP_Y);
        assertTrue(GridStructure.isValid(hexTopology, new GridSize(8, 8)));
        assertTrue(GridStructure.isValid(hexTopology, new GridSize(10, 9)));
        assertFalse(GridStructure.isValid(hexTopology, new GridSize(9, 8))); // 9 not multiple of 2
    }

    @Test
    void testInvalidWidthMultipleThrowsException() {
        GridTopology hexTopology = new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_X_WRAP_Y);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new GridStructure(hexTopology, new GridSize(9, 8)));
        assertTrue(ex.getMessage().contains("multiple of 2"));
    }

    @Test
    void testInvalidHeightMultipleThrowsException() {
        GridTopology triTopology = new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_WRAP_Y);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new GridStructure(triTopology, new GridSize(8, 10)));
        assertTrue(ex.getMessage().contains("multiple of 4"));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullArgumentsThrowException() {
        GridTopology topology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y);
        GridSize size = new GridSize(8, 8);

        assertThrows(NullPointerException.class, () -> new GridStructure(null, size));
        assertThrows(NullPointerException.class, () -> new GridStructure(topology, null));
        assertThrows(NullPointerException.class, () -> new GridStructure(null, null));
    }

}
