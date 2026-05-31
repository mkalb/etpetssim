package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.*;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
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
    void testFillAndClear() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        model.fill(TestEntity.WALL);

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), model.nonDefaultCoordinates().size())
        );

        model.clear();

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), model.countEntities(entity -> entity == TestEntity.EMPTY)),
                () -> assertTrue(model.nonDefaultCoordinates().isEmpty())
        );
    }

    @Test
    void testFillWithSupplierInvokesSupplierPerCell() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        AtomicInteger calls = new AtomicInteger();

        model.fill(() -> ((calls.getAndIncrement() % 2) == 0) ? TestEntity.WALL : TestEntity.FOOD);

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), calls.get()),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount() / 2, model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount() / 2, model.countEntities(entity -> entity == TestEntity.FOOD))
        );
    }

    @Test
    void testFillWithMapperUsesCoordinate() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        model.fill(c -> (c.x() == c.y()) ? TestEntity.WALL : TestEntity.EMPTY);

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.size().width(), model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinate(0, 0))),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinate(7, 7))),
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinate(7, 6)))
        );
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
    void testCellsReturnsAllCells() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(0, 0), TestEntity.WALL);

        List<GridCell<TestEntity>> cells = model.cells().toList();

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), cells.size()),
                () -> assertTrue(cells.contains(new GridCell<>(coordinate(0, 0), TestEntity.WALL))),
                () -> assertTrue(cells.contains(new GridCell<>(coordinate(7, 7), TestEntity.EMPTY))),
                () -> assertFalse(cells.contains(new GridCell<>(coordinate(0, 0), TestEntity.EMPTY)))
        );
    }

    @Test
    void testCellsIsInRowMajorOrder() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        List<GridCell<TestEntity>> cells = model.cells().toList();

        // cells() uses nested IntStream in row-major order: (x=0,y=0), (x=1,y=0), ..., (x=7,y=7)
        assertAll(
                () -> assertEquals(coordinate(0, 0), cells.getFirst().coordinate()),
                () -> assertEquals(coordinate(7, 7), cells.getLast().coordinate()),
                () -> assertEquals(coordinate(1, 0), cells.get(1).coordinate()),
                () -> assertEquals(coordinate(0, 1), cells.get(8).coordinate())
        );
    }

    @Test
    void testNonDefaultCellsContainsOnlyNonDefaultEntities() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);

        // nonDefaultCells() uses explicit nested loop in row-major order.
        List<GridCell<TestEntity>> nonDefaultCells = model.nonDefaultCells().toList();

        assertAll(
                () -> assertEquals(2, nonDefaultCells.size()),
                () -> assertTrue(nonDefaultCells.contains(new GridCell<>(coordinate(1, 1), TestEntity.WALL))),
                () -> assertTrue(nonDefaultCells.contains(new GridCell<>(coordinate(2, 2), TestEntity.FOOD)))
        );
    }

    @Test
    void testNonDefaultCellsIsEmptyWhenAllDefault() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        assertTrue(model.nonDefaultCells().toList().isEmpty());
    }

    @Test
    void testNonDefaultCoordinatesIsMutable() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);

        Set<GridCoordinate> coordinates = model.nonDefaultCoordinates();

        assertAll(
                () -> assertEquals(Set.of(coordinate(1, 1)), coordinates),
                () -> assertDoesNotThrow(() -> coordinates.add(coordinate(0, 0)))
        );
    }

    @Test
    void testNonDefaultCoordinatesSnapshotIsIndependentOfModelMutation() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);

        Set<GridCoordinate> snapshot = model.nonDefaultCoordinates();
        // Mutate the model after obtaining the snapshot.
        model.setEntityToDefault(coordinate(1, 1));

        // Snapshot must still reflect the state at the time of the call.
        assertEquals(2, snapshot.size());
    }

    @Test
    void testCountEntitiesMatchesExpectedCount() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.WALL);

        assertAll(
                () -> assertEquals(2, model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount() - 2, model.countEntities(entity -> entity == TestEntity.EMPTY)),
                () -> assertEquals(0, model.countEntities(entity -> entity == TestEntity.FOOD))
        );
    }

    @Test
    void testFilteredCoordinatesReturnsMatchingCoordinates() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(3, 3), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);

        // ArrayGridModel iterates in row-major order: y=1 row (x=1) comes before y=3 row (x=3).
        List<GridCoordinate> filtered = model.filteredCoordinates(entity -> entity == TestEntity.WALL);

        assertEquals(List.of(coordinate(1, 1), coordinate(3, 3)), filtered);
    }

    @Test
    void testFilteredCellsReturnsMatchingCells() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(3, 3), TestEntity.WALL);

        // ArrayGridModel iterates in row-major order: y=1 row comes before y=3 row.
        List<GridCell<TestEntity>> filtered = model.filteredCells(entity -> entity == TestEntity.WALL);

        assertEquals(
                List.of(
                        new GridCell<>(coordinate(1, 1), TestEntity.WALL),
                        new GridCell<>(coordinate(3, 3), TestEntity.WALL)
                ),
                filtered
        );
    }

    @Test
    void testFilteredCellsSortedBy() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(2, 1), TestEntity.WALL);
        model.setEntity(coordinate(0, 0), TestEntity.WALL);

        List<GridCell<TestEntity>> cells = model.filteredCellsSortedBy(
                entity -> entity == TestEntity.WALL,
                Comparator.comparing(GridCell::coordinate));

        assertEquals(List.of(coordinate(0, 0), coordinate(2, 1)),
                cells.stream().map(GridCell::coordinate).toList());
    }

    @Test
    void testFindRandomDefaultCoordinateReturnsEmpty() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.fill(TestEntity.WALL);

        Optional<GridCoordinate> result = model.findRandomDefaultCoordinate(new Random(7L));

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindRandomDefaultCoordinateReturnsOnlyDefaultCells() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
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
