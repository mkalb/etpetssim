package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridModelTestSupport.TestEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.SQUARE_STRUCTURE_8X8;
import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.coordinate;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
final class WritableGridModelTest {

    @ParameterizedTest
    @EnumSource
    void testStructure(GridModelImplementation implementation) {
        assertEquals(SQUARE_STRUCTURE_8X8, implementation.createModel().structure());
    }

    @ParameterizedTest
    @EnumSource
    void testIsCoordinateValid(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        assertAll(
                () -> assertTrue(model.isCoordinateValid(coordinate(0, 0))),
                () -> assertTrue(model.isCoordinateValid(coordinate(7, 7))),
                () -> assertFalse(model.isCoordinateValid(coordinate(8, 0))),
                () -> assertFalse(model.isCoordinateValid(coordinate(-1, 0)))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testDefaultEntity(GridModelImplementation implementation) {
        assertEquals(TestEntity.EMPTY, implementation.createModel().defaultEntity());
    }

    @ParameterizedTest
    @EnumSource
    void testGetEntityThrowsForInvalidCoordinate(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        assertThrows(IndexOutOfBoundsException.class, () -> model.getEntity(coordinate(8, 0)));
    }

    @ParameterizedTest
    @EnumSource
    void testIsDefaultEntityThrowsForInvalidCoordinate(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        assertThrows(IndexOutOfBoundsException.class, () -> model.isDefaultEntity(coordinate(8, 0)));
    }

    @ParameterizedTest
    @EnumSource
    void testIsSparse(GridModelImplementation implementation) {
        assertEquals(implementation == GridModelImplementation.SPARSE, implementation.createModel().isSparse());
    }

    @ParameterizedTest
    @EnumSource
    void testCountEntities(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.WALL);

        assertAll(
                () -> assertEquals(2, model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount() - 2, model.countEntities(entity -> entity == TestEntity.EMPTY)),
                () -> assertEquals(0, model.countEntities(entity -> entity == TestEntity.FOOD))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testNonDefaultCoordinatesIsMutable(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.setEntity(coordinate(1, 1), TestEntity.WALL);

        Set<GridCoordinate> coordinates = model.nonDefaultCoordinates();

        assertAll(
                () -> assertEquals(Set.of(coordinate(1, 1)), coordinates),
                () -> assertDoesNotThrow(() -> coordinates.add(coordinate(0, 0)))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testNonDefaultCoordinatesSnapshotIsIndependentOfModelMutation(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);

        Set<GridCoordinate> snapshot = model.nonDefaultCoordinates();
        model.setEntityToDefault(coordinate(1, 1));

        assertEquals(2, snapshot.size());
    }

    @ParameterizedTest
    @EnumSource
    void testFilteredCoordinates(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);

        List<GridCoordinate> walls = model.filteredCoordinates(entity -> entity == TestEntity.WALL);
        List<GridCoordinate> defaults = model.filteredCoordinates(entity -> entity == TestEntity.EMPTY);

        assertAll(
                () -> assertEquals(List.of(coordinate(1, 1)), walls),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount() - 2, defaults.size())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testFilteredCoordinatesIsMutableWithoutMatches(GridModelImplementation implementation) {
        List<GridCoordinate> coordinates = implementation.createModel()
                .filteredCoordinates(entity -> entity == TestEntity.WALL);

        assertAll(
                () -> assertTrue(coordinates.isEmpty()),
                () -> assertDoesNotThrow(() -> coordinates.add(coordinate(0, 0)))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testFindRandomDefaultCoordinateReturnsEmpty(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.fill(TestEntity.WALL);

        Optional<GridCoordinate> result = model.findRandomDefaultCoordinate(new Random(7L));

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @EnumSource
    void testFindRandomDefaultCoordinateReturnsOnlyDefaultCells(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
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

    @ParameterizedTest
    @EnumSource
    void testAllCellsReturnsAllCells(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.setEntity(coordinate(0, 0), TestEntity.WALL);

        List<GridCell<TestEntity>> cells = model.allCells();

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), cells.size()),
                () -> assertTrue(cells.contains(new GridCell<>(coordinate(0, 0), TestEntity.WALL))),
                () -> assertTrue(cells.contains(new GridCell<>(coordinate(7, 7), TestEntity.EMPTY))),
                () -> assertFalse(cells.contains(new GridCell<>(coordinate(0, 0), TestEntity.EMPTY)))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testAllCellsIsInRowMajorOrder(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();

        List<GridCell<TestEntity>> cells = model.allCells();

        assertAll(
                () -> assertEquals(coordinate(0, 0), cells.getFirst().coordinate()),
                () -> assertEquals(coordinate(7, 7), cells.getLast().coordinate()),
                () -> assertEquals(coordinate(1, 0), cells.get(1).coordinate()),
                () -> assertEquals(coordinate(0, 1), cells.get(8).coordinate())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testNonDefaultCellsContainsOnlyNonDefaultEntities(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);

        Set<GridCell<TestEntity>> nonDefaultCells = new HashSet<>(model.nonDefaultCells());

        assertAll(
                () -> assertEquals(2, nonDefaultCells.size()),
                () -> assertTrue(nonDefaultCells.contains(new GridCell<>(coordinate(1, 1), TestEntity.WALL))),
                () -> assertTrue(nonDefaultCells.contains(new GridCell<>(coordinate(2, 2), TestEntity.FOOD)))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testNonDefaultCellsIsEmptyWhenAllDefault(GridModelImplementation implementation) {
        assertTrue(implementation.createModel().nonDefaultCells().isEmpty());
    }

    @ParameterizedTest
    @EnumSource
    void testFilteredCells(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);

        List<GridCell<TestEntity>> walls = model.filteredCells(entity -> entity == TestEntity.WALL);
        List<GridCell<TestEntity>> defaults = model.filteredCells(entity -> entity == TestEntity.EMPTY);

        assertAll(
                () -> assertEquals(List.of(new GridCell<>(coordinate(1, 1), TestEntity.WALL)), walls),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount() - 2, defaults.size())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testFilteredCellsIsMutableWithoutMatches(GridModelImplementation implementation) {
        List<GridCell<TestEntity>> cells = implementation.createModel().filteredCells(entity -> entity == TestEntity.WALL);

        assertAll(
                () -> assertTrue(cells.isEmpty()),
                () -> assertDoesNotThrow(() -> cells.add(new GridCell<>(coordinate(0, 0), TestEntity.WALL)))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testFilteredCellsSortedBy(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.setEntity(coordinate(3, 3), TestEntity.WALL);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);

        List<GridCell<TestEntity>> sorted = model.filteredCellsSortedBy(
                entity -> entity == TestEntity.WALL,
                Comparator.comparing(GridCell::coordinate));

        assertEquals(
                List.of(
                        new GridCell<>(coordinate(1, 1), TestEntity.WALL),
                        new GridCell<>(coordinate(3, 3), TestEntity.WALL)
                ),
                sorted
        );
    }

    @ParameterizedTest
    @EnumSource
    void testFilteredCellsSortedByReturnsEmptyListWithoutMatches(GridModelImplementation implementation) {
        List<GridCell<TestEntity>> sorted = implementation.createModel().filteredCellsSortedBy(
                entity -> entity == TestEntity.WALL,
                Comparator.comparing(GridCell::coordinate));

        assertTrue(sorted.isEmpty());
    }

    @ParameterizedTest
    @EnumSource
    void testGetGridCellReturnsCoordinateAndEntity(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinate = coordinate(2, 3);
        model.setEntity(coordinate, TestEntity.WALL);

        GridCell<TestEntity> cell = model.getGridCell(coordinate);

        assertAll(
                () -> assertEquals(coordinate, cell.coordinate()),
                () -> assertEquals(TestEntity.WALL, cell.entity())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testGetGridCellReturnsDefaultEntity(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();

        GridCell<TestEntity> cell = model.getGridCell(coordinate(4, 4));

        assertAll(
                () -> assertEquals(coordinate(4, 4), cell.coordinate()),
                () -> assertEquals(TestEntity.EMPTY, cell.entity())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testCopy(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinate = coordinate(2, 0);
        model.setEntity(coordinate, TestEntity.WALL);

        WritableGridModel<TestEntity> clone = model.copy();
        TestEntity copiedEntity = clone.getEntity(coordinate);
        clone.setEntity(coordinate, TestEntity.FOOD);

        assertAll(
                () -> assertEquals(TestEntity.WALL, copiedEntity),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinate)),
                () -> assertEquals(TestEntity.FOOD, clone.getEntity(coordinate))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testCopyWithDefaultEntity(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinate = coordinate(2, 0);
        model.setEntity(coordinate, TestEntity.WALL);

        WritableGridModel<TestEntity> blankClone = model.copyWithDefaultEntity();

        assertAll(
                () -> assertEquals(model.structure(), blankClone.structure()),
                () -> assertEquals(model.defaultEntity(), blankClone.defaultEntity()),
                () -> assertTrue(blankClone.nonDefaultCoordinates().isEmpty()),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinate))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testSetEntityStoresNonDefaultEntity(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinate = coordinate(1, 1);
        boolean defaultBefore = model.isDefaultEntity(coordinate);

        model.setEntity(coordinate, TestEntity.WALL);

        assertAll(
                () -> assertTrue(defaultBefore),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinate)),
                () -> assertFalse(model.isDefaultEntity(coordinate)),
                () -> assertEquals(Set.of(coordinate), model.nonDefaultCoordinates())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testSetEntityRemovesCoordinateForDefaultEntity(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinate = coordinate(1, 1);
        model.setEntity(coordinate, TestEntity.WALL);

        model.setEntity(coordinate, TestEntity.EMPTY);

        assertAll(
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinate)),
                () -> assertTrue(model.nonDefaultCoordinates().isEmpty())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testSetEntityThrowsForInvalidCoordinate(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        assertThrows(IndexOutOfBoundsException.class, () -> model.setEntity(coordinate(8, 0), TestEntity.WALL));
    }

    @ParameterizedTest
    @EnumSource
    void testSetEntityWithGridCellUsesCellValues(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinate = coordinate(2, 0);

        model.setEntity(new GridCell<>(coordinate, TestEntity.FOOD));

        assertEquals(TestEntity.FOOD, model.getEntity(coordinate));
    }

    @ParameterizedTest
    @EnumSource
    void testSetEntityWithGridCellResetsToDefault(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinate = coordinate(1, 1);
        model.setEntity(coordinate, TestEntity.WALL);

        model.setEntity(new GridCell<>(coordinate, TestEntity.EMPTY));

        assertAll(
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinate)),
                () -> assertTrue(model.isDefaultEntity(coordinate))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testSetEntityToDefault(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinate = coordinate(1, 1);
        model.setEntity(coordinate, TestEntity.WALL);

        model.setEntityToDefault(coordinate);

        assertAll(
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinate)),
                () -> assertTrue(model.nonDefaultCoordinates().isEmpty())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testFill(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();

        model.fill(TestEntity.WALL);

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), model.nonDefaultCoordinates().size())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testFillResetsAllCellsForDefaultEntity(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.setEntity(coordinate(1, 1), TestEntity.WALL);

        model.fill(TestEntity.EMPTY);

        assertTrue(model.nonDefaultCoordinates().isEmpty());
    }

    @ParameterizedTest
    @EnumSource
    void testFillWithSupplier(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        AtomicInteger calls = new AtomicInteger();

        model.fill(() -> ((calls.getAndIncrement() % 2) == 0) ? TestEntity.WALL : TestEntity.FOOD);

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), calls.get()),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount() / 2, model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount() / 2, model.countEntities(entity -> entity == TestEntity.FOOD))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testFillWithMapper(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        Set<GridCoordinate> diagonalCoordinates = new HashSet<>();
        for (int index = 0; index < SQUARE_STRUCTURE_8X8.size().width(); index++) {
            diagonalCoordinates.add(coordinate(index, index));
        }

        model.fill(c -> (c.x() == c.y()) ? TestEntity.WALL : TestEntity.EMPTY);

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.size().width(), model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinate(0, 0))),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinate(7, 7))),
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinate(7, 6))),
                () -> assertEquals(diagonalCoordinates, model.nonDefaultCoordinates())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testClear(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        model.fill(TestEntity.WALL);

        model.clear();

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount(), model.countEntities(entity -> entity == TestEntity.EMPTY)),
                () -> assertTrue(model.nonDefaultCoordinates().isEmpty())
        );
    }

    @ParameterizedTest
    @EnumSource
    void testSetSwappedEntitiesWritesGivenEntities(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinateA = coordinate(0, 0);
        GridCoordinate coordinateB = coordinate(2, 1);

        model.setSwappedEntities(
                new GridCell<>(coordinateA, TestEntity.WALL),
                new GridCell<>(coordinateB, TestEntity.FOOD));

        assertAll(
                () -> assertEquals(TestEntity.FOOD, model.getEntity(coordinateA)),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinateB))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testSetSwappedEntitiesIgnoresCurrentModelState(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate coordinateA = coordinate(0, 0);
        GridCoordinate coordinateB = coordinate(2, 1);

        model.setEntity(coordinateA, TestEntity.WALL);
        model.setEntity(coordinateB, TestEntity.FOOD);

        model.setSwappedEntities(
                new GridCell<>(coordinateA, TestEntity.EMPTY),
                new GridCell<>(coordinateB, TestEntity.EMPTY));

        assertAll(
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinateA)),
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinateB))
        );
    }

    @ParameterizedTest
    @EnumSource
    void testSetSwappedEntitiesThrowsForInvalidCoordinate(GridModelImplementation implementation) {
        WritableGridModel<TestEntity> model = implementation.createModel();
        GridCoordinate valid = coordinate(0, 0);
        GridCoordinate invalid = coordinate(8, 0);

        model.setEntity(valid, TestEntity.WALL);

        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class,
                        () -> model.setSwappedEntities(
                                new GridCell<>(invalid, TestEntity.FOOD),
                                new GridCell<>(valid, TestEntity.EMPTY))),
                () -> assertThrows(IndexOutOfBoundsException.class,
                        () -> model.setSwappedEntities(
                                new GridCell<>(valid, TestEntity.EMPTY),
                                new GridCell<>(invalid, TestEntity.FOOD)))
        );
        assertEquals(TestEntity.WALL, model.getEntity(valid));
    }

    enum GridModelImplementation {
        ARRAY,
        SPARSE;

        WritableGridModel<TestEntity> createModel() {
            return switch (this) {
                case ARRAY -> new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
                case SPARSE -> new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
            };
        }
    }

}
