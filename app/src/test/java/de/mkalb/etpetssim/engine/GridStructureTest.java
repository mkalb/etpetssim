package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
final class GridStructureTest {

    private static GridStructure createStructure(CellShape cellShape, GridEdgeBehavior gridEdgeBehavior,
                                                 int width, int height) {
        return new GridStructure(new GridTopology(cellShape, gridEdgeBehavior), new GridSize(width, height));
    }

    private static void assertRowMajorCoordinates(GridStructure structure, List<GridCoordinate> coordinates) {
        assertAll(
                () -> assertEquals(20 * 30, coordinates.size()),
                () -> assertEquals(new GridCoordinate(0, 0), coordinates.getFirst()),
                () -> assertEquals(new GridCoordinate(1, 0), coordinates.get(1)),
                () -> assertEquals(new GridCoordinate(19, 0), coordinates.get(19)),
                () -> assertEquals(new GridCoordinate(0, 1), coordinates.get(20)),
                () -> assertEquals(new GridCoordinate(19, 29), coordinates.getLast()),
                () -> assertEquals(coordinates.size(), new HashSet<>(coordinates).size()),
                () -> assertTrue(coordinates.stream().allMatch(structure::isCoordinateValid))
        );
    }

    @Test
    void testRecord() {
        GridTopology topology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_X_BLOCK_Y);
        GridSize size = new GridSize(20, 30);
        GridStructure structure = new GridStructure(topology, size);

        assertAll(
                () -> assertEquals(topology, structure.topology()),
                () -> assertEquals(size, structure.size()),
                () -> assertDoesNotThrow(() -> createStructure(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_XY, 9, 11)),
                () -> assertDoesNotThrow(() -> createStructure(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY, 9, 11)),
                () -> assertDoesNotThrow(() -> createStructure(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_XY, 9, 11)),
                () -> assertDoesNotThrow(() -> createStructure(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY, 8, 8)),
                () -> assertDoesNotThrow(() -> createStructure(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY, 9, 11)),
                () -> assertDoesNotThrow(() -> createStructure(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY, 8, 8)),
                () -> assertDoesNotThrow(() -> createStructure(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY, 10, 12)),
                () -> assertDoesNotThrow(() -> createStructure(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY, 8, 8)),
                () -> assertDoesNotThrow(() -> createStructure(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY, 10, 9))
        );
    }

    @Test
    void testConstructorRejectsInvalidWidthMultiple() {
        assertThrows(IllegalArgumentException.class,
                () -> createStructure(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY, 9, 8));
    }

    @Test
    void testConstructorRejectsInvalidHeightMultiple() {
        assertThrows(IllegalArgumentException.class,
                () -> createStructure(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY, 8, 9));
    }

    @Test
    void testIsValid() {
        GridTopology squareTopology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY);
        GridTopology triangleTopology = new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_XY);
        GridTopology hexagonTopology = new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY);

        assertAll(
                () -> assertTrue(GridStructure.isValid(
                        new GridTopology(CellShape.TRIANGLE, GridEdgeBehavior.BLOCK_XY), new GridSize(9, 11))),
                () -> assertTrue(GridStructure.isValid(
                        new GridTopology(CellShape.HEXAGON, GridEdgeBehavior.BLOCK_XY), new GridSize(9, 11))),
                () -> assertTrue(GridStructure.isValid(squareTopology, new GridSize(9, 11))),
                () -> assertTrue(GridStructure.isValid(triangleTopology, new GridSize(10, 12))),
                () -> assertFalse(GridStructure.isValid(triangleTopology, new GridSize(9, 8))),
                () -> assertFalse(GridStructure.isValid(triangleTopology, new GridSize(8, 9))),
                () -> assertTrue(GridStructure.isValid(hexagonTopology, new GridSize(10, 9))),
                () -> assertFalse(GridStructure.isValid(hexagonTopology, new GridSize(9, 8)))
        );
    }

    @Test
    void testCellShape() {
        assertEquals(CellShape.TRIANGLE,
                createStructure(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_BLOCK_Y, 20, 30).cellShape());
    }

    @Test
    void testCellCount() {
        assertAll(
                () -> assertEquals(20 * 30,
                        createStructure(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY, 20, 30).cellCount()),
                () -> assertEquals(12 * 16,
                        createStructure(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY, 12, 16).cellCount())
        );
    }

    @Test
    void testEdgeBehaviorX() {
        assertEquals(EdgeBehavior.WRAP,
                createStructure(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_BLOCK_Y, 20, 30).edgeBehaviorX());
    }

    @Test
    void testEdgeBehaviorY() {
        assertEquals(EdgeBehavior.BLOCK,
                createStructure(CellShape.TRIANGLE, GridEdgeBehavior.WRAP_X_BLOCK_Y, 20, 30).edgeBehaviorY());
    }

    @Test
    void testMinCoordinateInclusive() {
        assertEquals(new GridCoordinate(0, 0),
                createStructure(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY, 20, 30).minCoordinateInclusive());
    }

    @Test
    void testMaxCoordinateExclusive() {
        assertEquals(new GridCoordinate(20, 30),
                createStructure(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY, 20, 30).maxCoordinateExclusive());
    }

    @Test
    void testMaxCoordinateInclusive() {
        assertEquals(new GridCoordinate(19, 29),
                createStructure(CellShape.TRIANGLE, GridEdgeBehavior.ABSORB_XY, 20, 30).maxCoordinateInclusive());
    }

    @Test
    void testIsCoordinateValid() {
        GridStructure structure = createStructure(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY, 20, 30);

        assertAll(
                () -> assertTrue(structure.isCoordinateValid(new GridCoordinate(0, 0))),
                () -> assertTrue(structure.isCoordinateValid(new GridCoordinate(0, 29))),
                () -> assertTrue(structure.isCoordinateValid(new GridCoordinate(19, 29))),
                () -> assertTrue(structure.isCoordinateValid(new GridCoordinate(19, 0))),
                () -> assertTrue(structure.isCoordinateValid(new GridCoordinate(10, 15))),
                () -> assertFalse(structure.isCoordinateValid(new GridCoordinate(-1, -1))),
                () -> assertFalse(structure.isCoordinateValid(new GridCoordinate(-1, 0))),
                () -> assertFalse(structure.isCoordinateValid(new GridCoordinate(0, -1))),
                () -> assertFalse(structure.isCoordinateValid(new GridCoordinate(20, 0))),
                () -> assertFalse(structure.isCoordinateValid(new GridCoordinate(0, 30))),
                () -> assertFalse(structure.isCoordinateValid(new GridCoordinate(20, 30)))
        );
    }

    @Test
    void testCoordinatesStream() {
        GridStructure structure = createStructure(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY, 20, 30);

        assertRowMajorCoordinates(structure, structure.coordinatesStream().toList());
    }

    @Test
    void testCoordinatesList() {
        GridStructure structure = createStructure(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY, 20, 30);

        assertRowMajorCoordinates(structure, structure.coordinatesList());
    }

    @Test
    void testToDisplayString() {
        assertAll(
                () -> assertEquals("[HEXAGON WRAP] 20 × 30",
                        createStructure(CellShape.HEXAGON, GridEdgeBehavior.WRAP_XY, 20, 30).toDisplayString()),
                () -> assertEquals("[SQUARE BLOCK/WRAP] 32 × 32",
                        createStructure(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_WRAP_Y, 32, 32).toDisplayString())
        );
    }

}
