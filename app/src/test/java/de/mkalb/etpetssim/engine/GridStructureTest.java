package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class GridStructureTest {

    @Test
    void testRecord() {
        GridTopology topology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_X_BLOCK_Y);
        GridSize size = new GridSize(20, 30);
        GridStructure structure = new GridStructure(topology, size);

        assertEquals(topology, structure.topology());
        assertEquals(size, structure.size());
    }

    @Test
    void testConstructorRejectsInvalidWidthMultiple() {
        GridTopology hexTopology = new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new GridStructure(hexTopology, new GridSize(9, 8)));
        assertTrue(ex.getMessage().contains("multiple of 2"));
    }

    @Test
    void testConstructorRejectsInvalidHeightMultiple() {
        GridTopology triTopology = new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new GridStructure(triTopology, new GridSize(8, 9)));
        assertTrue(ex.getMessage().contains("multiple of 2"));
    }

    @Test
    void testConstructorAcceptsValidMultiplesForAllCellShapes() {
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(9, 11)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(9, 11)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_XY),
                new GridSize(9, 11)));

        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY),
                new GridSize(8, 8)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY),
                new GridSize(9, 11)));

        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY),
                new GridSize(8, 8)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY),
                new GridSize(10, 12)));

        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY),
                new GridSize(8, 8)));
        assertDoesNotThrow(() -> new GridStructure(
                new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY),
                new GridSize(10, 9)));
    }

    @Test
    void testIsValid() {
        assertTrue(GridStructure.isValid(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_XY), new GridSize(9, 11)));
        assertTrue(GridStructure.isValid(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(9, 11)));
        assertTrue(GridStructure.isValid(new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_XY), new GridSize(9, 11)));

        GridTopology squareTopology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY);
        assertTrue(GridStructure.isValid(squareTopology, new GridSize(8, 8)));
        assertTrue(GridStructure.isValid(squareTopology, new GridSize(9, 11)));

        GridTopology triTopology = new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY);
        assertTrue(GridStructure.isValid(triTopology, new GridSize(8, 8)));
        assertTrue(GridStructure.isValid(triTopology, new GridSize(10, 12)));
        assertFalse(GridStructure.isValid(triTopology, new GridSize(8, 9)));

        GridTopology hexTopology = new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY);
        assertTrue(GridStructure.isValid(hexTopology, new GridSize(8, 8)));
        assertTrue(GridStructure.isValid(hexTopology, new GridSize(10, 9)));
        assertFalse(GridStructure.isValid(hexTopology, new GridSize(9, 8)));
    }

    @Test
    void testCellShape() {
        assertEquals(CellShape.TRIANGLE, new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_BLOCK_Y),
                new GridSize(20, 30)).cellShape());
    }

    @Test
    void testCellCount() {
        assertEquals(20 * 30, new GridStructure(new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY), new GridSize(20, 30)).cellCount());
        assertEquals(30 * 20, new GridStructure(new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY), new GridSize(30, 20)).cellCount());
        assertEquals(12 * 16, new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY), new GridSize(12, 16)).cellCount());
    }

    @Test
    void testEdgeBehaviorX() {
        assertEquals(EdgeBehavior.WRAP, new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_BLOCK_Y),
                new GridSize(20, 30)).edgeBehaviorX());
    }

    @Test
    void testEdgeBehaviorY() {
        assertEquals(EdgeBehavior.BLOCK, new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_BLOCK_Y),
                new GridSize(20, 30)).edgeBehaviorY());
    }

    @Test
    void testMinCoordinateInclusive() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY),
                new GridSize(20, 30));

        assertEquals(new GridCoordinate(0, 0), structure.minCoordinateInclusive());
    }

    @Test
    void testMaxCoordinateExclusive() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY),
                new GridSize(20, 30));

        assertEquals(new GridCoordinate(20, 30), structure.maxCoordinateExclusive());
    }

    @Test
    void testMaxCoordinateInclusive() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY),
                new GridSize(20, 30));

        assertEquals(new GridCoordinate(19, 29), structure.maxCoordinateInclusive());
    }

    @Test
    void testIsCoordinateValid() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
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
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(20, 30));

        List<GridCoordinate> coordinates = structure.coordinatesStream().toList();
        assertEquals(20 * 30, coordinates.size());
        assertTrue(coordinates.contains(new GridCoordinate(10, 15)));
        assertEquals(new GridCoordinate(0, 0), coordinates.getFirst());
        assertEquals(new GridCoordinate(1, 0), coordinates.get(1));
        assertEquals(new GridCoordinate(19, 0), coordinates.get(19));
        assertEquals(new GridCoordinate(0, 1), coordinates.get(20));
        assertEquals(new GridCoordinate(19, 29), coordinates.getLast());

        Set<GridCoordinate> unique = new HashSet<>(coordinates);
        assertEquals(coordinates.size(), unique.size());
        for (GridCoordinate c : coordinates) {
            assertTrue(structure.isCoordinateValid(c));
        }
    }

    @Test
    void testCoordinatesList() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(20, 30));

        List<GridCoordinate> coordinates = structure.coordinatesList();
        assertEquals(20 * 30, coordinates.size());
        assertTrue(coordinates.contains(new GridCoordinate(10, 15)));
        assertEquals(new GridCoordinate(0, 0), coordinates.getFirst());
        assertEquals(new GridCoordinate(1, 0), coordinates.get(1));
        assertEquals(new GridCoordinate(19, 0), coordinates.get(19));
        assertEquals(new GridCoordinate(0, 1), coordinates.get(20));
        assertEquals(new GridCoordinate(19, 29), coordinates.getLast());

        Set<GridCoordinate> unique = new HashSet<>(coordinates);
        assertEquals(coordinates.size(), unique.size());
        for (GridCoordinate c : coordinates) {
            assertTrue(structure.isCoordinateValid(c));
        }
    }

    @Test
    void testToDisplayString() {
        assertEquals("[HEXAGON WRAP] 20 × 30", new GridStructure(
                new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY),
                new GridSize(20, 30)).toDisplayString());
        assertEquals("[TRIANGLE BLOCK] 12 × 16", new GridStructure(
                new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(12, 16)).toDisplayString());
        assertEquals("[SQUARE BLOCK/WRAP] 32 × 32", new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y),
                new GridSize(32, 32)).toDisplayString());
    }

}
