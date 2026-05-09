package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

final class ArrayGridModelTest {

    @Test
    void testSetGetAndDefaultEntityHandling() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(1, 1);

        assertEquals(TestEntity.EMPTY, model.getEntity(coordinate));
        assertTrue(model.isDefaultEntity(coordinate));

        model.setEntity(coordinate, TestEntity.WALL);

        assertEquals(TestEntity.WALL, model.getEntity(coordinate));
        assertFalse(model.isDefaultEntity(coordinate));
        assertEquals(Set.of(coordinate), model.nonDefaultCoordinates());

        model.setEntityToDefault(coordinate);

        assertEquals(TestEntity.EMPTY, model.getEntity(coordinate));
        assertTrue(model.nonDefaultCoordinates().isEmpty());
    }

    @Test
    void testCopyAndCopyWithDefaultEntity() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(2, 0);
        model.setEntity(coordinate, TestEntity.WALL);

        WritableGridModel<TestEntity> clone = model.copy();
        WritableGridModel<TestEntity> blankClone = model.copyWithDefaultEntity();

        assertEquals(TestEntity.WALL, clone.getEntity(coordinate));
        assertEquals(TestEntity.EMPTY, blankClone.getEntity(coordinate));

        clone.setEntity(coordinate, TestEntity.FOOD);
        assertEquals(TestEntity.WALL, model.getEntity(coordinate));
        assertEquals(TestEntity.FOOD, clone.getEntity(coordinate));
    }

    @Test
    void testFilteredAndSortedCells() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(2, 1), TestEntity.WALL);
        model.setEntity(coordinate(0, 0), TestEntity.WALL);

        List<GridCell<TestEntity>> cells = model.filteredAndSortedCells(
                entity -> entity == TestEntity.WALL,
                Comparator.comparing(GridCell::coordinate));

        assertEquals(List.of(coordinate(0, 0), coordinate(2, 1)),
                cells.stream().map(GridCell::coordinate).toList());
    }

    @Test
    void testSwapInputCellEntitiesWritesInputEntityValues() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinateA = coordinate(0, 0);
        GridCoordinate coordinateB = coordinate(1, 0);

        model.swapInputCellEntities(
                new GridCell<>(coordinateA, TestEntity.WALL),
                new GridCell<>(coordinateB, TestEntity.FOOD));

        assertEquals(TestEntity.FOOD, model.getEntity(coordinateA));
        assertEquals(TestEntity.WALL, model.getEntity(coordinateB));
    }

    @Test
    void testSwapInputCellEntitiesIgnoresCurrentModelState() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinateA = coordinate(0, 0);
        GridCoordinate coordinateB = coordinate(1, 0);

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
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
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
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate invalid = coordinate(8, 0);

        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class, () -> model.getEntity(invalid)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> model.setEntity(invalid, TestEntity.WALL)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> model.isDefaultEntity(invalid))
        );
    }

}

