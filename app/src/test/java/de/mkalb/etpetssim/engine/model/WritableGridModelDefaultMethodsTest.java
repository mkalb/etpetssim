package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the interface default methods that remain in {@link ReadableGridModel},
 * {@link WritableGridModel}, and {@link GridModel}:
 * {@code isComposite()}, {@code isCoordinateValid()}, {@code getGridCell()},
 * and {@code setEntity(GridCell)}.
 * <p>
 * Both {@link SparseGridModel} and {@link ArrayGridModel} are used to verify that
 * the default implementations behave correctly for all concrete models.
 */
final class WritableGridModelDefaultMethodsTest {

    // --- isComposite() ---

    @Test
    void testIsCompositeReturnsFalseForSparseModel() {
        assertFalse(new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY).isComposite());
    }

    @Test
    void testIsCompositeReturnsFalseForArrayModel() {
        assertFalse(new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY).isComposite());
    }

    // --- isCoordinateValid() ---

    @Test
    void testIsCoordinateValidDelegatesToStructureForSparseModel() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        assertAll(
                () -> assertTrue(model.isCoordinateValid(coordinate(0, 0))),
                () -> assertTrue(model.isCoordinateValid(coordinate(7, 7))),
                () -> assertFalse(model.isCoordinateValid(coordinate(8, 0))),
                () -> assertFalse(model.isCoordinateValid(coordinate(-1, 0)))
        );
    }

    @Test
    void testIsCoordinateValidDelegatesToStructureForArrayModel() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        assertAll(
                () -> assertTrue(model.isCoordinateValid(coordinate(0, 0))),
                () -> assertTrue(model.isCoordinateValid(coordinate(7, 7))),
                () -> assertFalse(model.isCoordinateValid(coordinate(8, 0))),
                () -> assertFalse(model.isCoordinateValid(coordinate(-1, 0)))
        );
    }

    // --- getGridCell() ---

    @Test
    void testGetGridCellReturnsCoordinateAndEntityForSparseModel() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(2, 3);
        model.setEntity(coordinate, TestEntity.WALL);

        GridCell<TestEntity> cell = model.getGridCell(coordinate);

        assertAll(
                () -> assertEquals(coordinate, cell.coordinate()),
                () -> assertEquals(TestEntity.WALL, cell.entity())
        );
    }

    @Test
    void testGetGridCellReturnsCoordinateAndEntityForArrayModel() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(2, 3);
        model.setEntity(coordinate, TestEntity.WALL);

        GridCell<TestEntity> cell = model.getGridCell(coordinate);

        assertAll(
                () -> assertEquals(coordinate, cell.coordinate()),
                () -> assertEquals(TestEntity.WALL, cell.entity())
        );
    }

    @Test
    void testGetGridCellReturnsDefaultEntityForSparseModel() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        GridCell<TestEntity> cell = model.getGridCell(coordinate(4, 4));

        assertAll(
                () -> assertEquals(coordinate(4, 4), cell.coordinate()),
                () -> assertEquals(TestEntity.EMPTY, cell.entity())
        );
    }

    @Test
    void testGetGridCellReturnsDefaultEntityForArrayModel() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        GridCell<TestEntity> cell = model.getGridCell(coordinate(4, 4));

        assertAll(
                () -> assertEquals(coordinate(4, 4), cell.coordinate()),
                () -> assertEquals(TestEntity.EMPTY, cell.entity())
        );
    }

    // --- setEntity(GridCell) ---

    @Test
    void testSetEntityWithGridCellUsesCellValuesForSparseModel() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(2, 0);

        model.setEntity(new GridCell<>(coordinate, TestEntity.FOOD));

        assertEquals(TestEntity.FOOD, model.getEntity(coordinate));
    }

    @Test
    void testSetEntityWithGridCellUsesCellValuesForArrayModel() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(2, 0);

        model.setEntity(new GridCell<>(coordinate, TestEntity.FOOD));

        assertEquals(TestEntity.FOOD, model.getEntity(coordinate));
    }

    @Test
    void testSetEntityWithGridCellToDefaultForSparseModel() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(1, 1);
        model.setEntity(coordinate, TestEntity.WALL);

        model.setEntity(new GridCell<>(coordinate, TestEntity.EMPTY));

        assertAll(
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinate)),
                () -> assertTrue(model.isDefaultEntity(coordinate))
        );
    }

    @Test
    void testSetEntityWithGridCellToDefaultForArrayModel() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(1, 1);
        model.setEntity(coordinate, TestEntity.WALL);

        model.setEntity(new GridCell<>(coordinate, TestEntity.EMPTY));

        assertAll(
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinate)),
                () -> assertTrue(model.isDefaultEntity(coordinate))
        );
    }

}
