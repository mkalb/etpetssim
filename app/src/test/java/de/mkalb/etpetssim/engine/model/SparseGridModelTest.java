package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

final class SparseGridModelTest {

    @Test
    void testSetGetAndDefaultEntityHandling() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(1, 1);

        assertEquals(TestEntity.EMPTY, model.getEntity(coordinate));
        assertTrue(model.isDefaultEntity(coordinate));

        model.setEntity(coordinate, TestEntity.WALL);

        assertEquals(TestEntity.WALL, model.getEntity(coordinate));
        assertFalse(model.isDefaultEntity(coordinate));
        assertEquals(Set.of(coordinate), model.nonDefaultCoordinates());

        model.setEntity(coordinate, TestEntity.EMPTY);

        assertEquals(TestEntity.EMPTY, model.getEntity(coordinate));
        assertTrue(model.nonDefaultCoordinates().isEmpty());
    }

    @Test
    void testFillAndClear() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        model.fill(TestEntity.WALL);
        assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), model.nonDefaultCoordinates().size());

        model.clear();
        assertTrue(model.nonDefaultCoordinates().isEmpty());

        model.fill(coordinate -> (coordinate.x() == coordinate.y()) ? TestEntity.FOOD : TestEntity.EMPTY);
        Set<GridCoordinate> diagonalCoordinates = new HashSet<>();
        for (int index = 0; index < SQUARE_STRUCTURE_8X8.size().width(); index++) {
            diagonalCoordinates.add(coordinate(index, index));
        }
        assertEquals(diagonalCoordinates, model.nonDefaultCoordinates());
    }

    @Test
    void testFilteredCoordinatesIncludeDefaultCells() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate wallCoordinate = coordinate(2, 1);
        model.setEntity(wallCoordinate, TestEntity.WALL);

        List<GridCoordinate> coordinates = model.filteredCoordinates(entity -> entity == TestEntity.EMPTY);

        assertEquals(SQUARE_STRUCTURE_8X8.cellCount() - 1, coordinates.size());
        assertFalse(coordinates.contains(wallCoordinate));
    }

    @Test
    void testFindRandomDefaultCoordinateReturnsEmpty() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.fill(TestEntity.WALL);

        Optional<GridCoordinate> result = model.findRandomDefaultCoordinate(new Random(7L));

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindRandomDefaultCoordinateReturnsOnlyDefaultCells() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.fill(TestEntity.WALL);
        GridCoordinate onlyDefaultCoordinate = coordinate(3, 5);
        model.setEntityToDefault(onlyDefaultCoordinate);

        Optional<GridCoordinate> result = model.findRandomDefaultCoordinate(new Random(13L));

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals(onlyDefaultCoordinate, result.orElseThrow()),
                () -> assertTrue(model.isDefaultEntity(result.orElseThrow()))
        );
    }

    @Test
    void testSwapInputCellEntitiesWritesInputEntityValues() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinateA = coordinate(0, 0);
        GridCoordinate coordinateB = coordinate(2, 1);

        model.swapInputCellEntities(
                new GridCell<>(coordinateA, TestEntity.WALL),
                new GridCell<>(coordinateB, TestEntity.FOOD));

        assertEquals(TestEntity.FOOD, model.getEntity(coordinateA));
        assertEquals(TestEntity.WALL, model.getEntity(coordinateB));
    }

    @Test
    void testSwapInputCellEntitiesIgnoresCurrentModelState() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinateA = coordinate(0, 0);
        GridCoordinate coordinateB = coordinate(2, 1);

        model.setEntity(coordinateA, TestEntity.WALL);
        model.setEntity(coordinateB, TestEntity.FOOD);

        model.swapInputCellEntities(
                new GridCell<>(coordinateA, TestEntity.EMPTY),
                new GridCell<>(coordinateB, TestEntity.EMPTY));

        assertEquals(TestEntity.EMPTY, model.getEntity(coordinateA));
        assertEquals(TestEntity.EMPTY, model.getEntity(coordinateB));
    }

    @Test
    void testSwapInputCellEntitiesThrowsForInvalidCoordinate() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate valid = coordinate(0, 0);
        GridCoordinate invalid = coordinate(8, 0);

        model.setEntity(valid, TestEntity.WALL);

        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class,
                        () -> model.swapInputCellEntities(
                                new GridCell<>(invalid, TestEntity.FOOD),
                                new GridCell<>(valid, TestEntity.EMPTY))),
                () -> assertThrows(IndexOutOfBoundsException.class,
                        () -> model.swapInputCellEntities(
                                new GridCell<>(valid, TestEntity.EMPTY),
                                new GridCell<>(invalid, TestEntity.FOOD)))
        );
        assertEquals(TestEntity.WALL, model.getEntity(valid));
    }

    @Test
    void testInvalidCoordinateThrowsException() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate invalid = coordinate(8, 0);

        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class, () -> model.getEntity(invalid)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> model.setEntity(invalid, TestEntity.WALL)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> model.isDefaultEntity(invalid))
        );
    }

}

