package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.*;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

final class WritableGridModelDefaultMethodsTest {

    private static final int RANDOM_SEED = 123;
    private static final int GRID_CELL_COUNT = 64;
    private static final int HALF_GRID_CELL_COUNT = GRID_CELL_COUNT / 2;
    private static final int SAMPLE_NON_DEFAULT_CELL_COUNT = 3;

    private static DefaultMethodModel createModelWithSampleEntities() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);
        model.setEntity(coordinate(3, 3), TestEntity.WALL);
        return model;
    }

    @Test
    void testIsCompositeReturnsFalse() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        assertFalse(model.isComposite());
    }

    @Test
    void testIsCoordinateValidDelegatesToStructure() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        assertAll(
                () -> assertTrue(model.isCoordinateValid(coordinate(0, 0))),
                () -> assertTrue(model.isCoordinateValid(coordinate(7, 7))),
                () -> assertFalse(model.isCoordinateValid(coordinate(8, 0))),
                () -> assertFalse(model.isCoordinateValid(coordinate(-1, 0)))
        );
    }

    @Test
    void testGetGridCellReturnsCoordinateAndEntity() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(2, 3);
        model.setEntity(coordinate, TestEntity.WALL);

        GridCell<TestEntity> cell = model.getGridCell(coordinate);

        assertAll(
                () -> assertEquals(coordinate, cell.coordinate()),
                () -> assertEquals(TestEntity.WALL, cell.entity())
        );
    }

    @Test
    void testIsDefaultEntityReflectsCellState() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(1, 2);

        assertTrue(model.isDefaultEntity(coordinate));
        model.setEntity(coordinate, TestEntity.FOOD);
        assertFalse(model.isDefaultEntity(coordinate));
    }

    @Test
    void testCellsReturnsAllCoordinates() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(0, 0), TestEntity.WALL);

        List<GridCell<TestEntity>> cells = model.cells().toList();

        assertAll(
                () -> assertEquals(GRID_CELL_COUNT, cells.size()),
                () -> assertTrue(cells.contains(new GridCell<>(coordinate(0, 0), TestEntity.WALL))),
                () -> assertTrue(cells.contains(new GridCell<>(coordinate(7, 7), TestEntity.EMPTY)))
        );
    }

    @Test
    void testNonDefaultCellsReturnsOnlyNonDefaultEntities() {
        DefaultMethodModel model = createModelWithSampleEntities();

        List<GridCell<TestEntity>> nonDefaultCells = model.nonDefaultCells().toList();

        assertAll(
                () -> assertEquals(3, nonDefaultCells.size()),
                () -> assertTrue(nonDefaultCells.contains(new GridCell<>(coordinate(1, 1), TestEntity.WALL))),
                () -> assertTrue(nonDefaultCells.contains(new GridCell<>(coordinate(2, 2), TestEntity.FOOD))),
                () -> assertTrue(nonDefaultCells.contains(new GridCell<>(coordinate(3, 3), TestEntity.WALL)))
        );
    }

    @Test
    void testNonDefaultCoordinatesReturnsUnmodifiableSet() {
        DefaultMethodModel model = createModelWithSampleEntities();

        Set<GridCoordinate> coordinates = model.nonDefaultCoordinates();

        assertAll(
                () -> assertEquals(Set.of(coordinate(1, 1), coordinate(2, 2), coordinate(3, 3)), coordinates),
                () -> assertThrows(UnsupportedOperationException.class, () -> coordinates.add(coordinate(4, 4)))
        );
    }

    @Test
    void testCountCellsCountsMatchingGridCells() {
        DefaultMethodModel model = createModelWithSampleEntities();

        long count = model.countCells(cell -> cell.entity() == TestEntity.WALL);

        assertEquals(2, count);
    }

    @Test
    void testCountEntitiesCountsMatchingEntities() {
        DefaultMethodModel model = createModelWithSampleEntities();

        long count = model.countEntities(entity -> entity == TestEntity.EMPTY);

        assertEquals(GRID_CELL_COUNT - SAMPLE_NON_DEFAULT_CELL_COUNT, count);
    }

    @Test
    void testFilteredCoordinatesReturnsMatchingCoordinates() {
        DefaultMethodModel model = createModelWithSampleEntities();

        List<GridCoordinate> filtered = model.filteredCoordinates(entity -> entity == TestEntity.WALL);

        assertEquals(List.of(coordinate(1, 1), coordinate(3, 3)), filtered);
    }

    @Test
    void testFilteredCellsReturnsMatchingCells() {
        DefaultMethodModel model = createModelWithSampleEntities();

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
    void testFilteredCellsSortedByReturnsSortedCells() {
        DefaultMethodModel model = createModelWithSampleEntities();

        List<GridCell<TestEntity>> sorted = model.filteredCellsSortedBy(
                entity -> entity == TestEntity.WALL,
                Comparator.comparingInt((GridCell<TestEntity> cell) -> cell.coordinate().x()).reversed()
        );

        assertEquals(
                List.of(
                        new GridCell<>(coordinate(3, 3), TestEntity.WALL),
                        new GridCell<>(coordinate(1, 1), TestEntity.WALL)
                ),
                sorted
        );
    }

    @Test
    void testFindRandomDefaultCoordinateReturnsEmptyWhenNoDefaultExists() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.fill(TestEntity.WALL);

        Optional<GridCoordinate> result = model.findRandomDefaultCoordinate(new Random(RANDOM_SEED));

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindRandomDefaultCoordinateReturnsDefaultCoordinate() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.fill(TestEntity.WALL);
        GridCoordinate onlyDefaultCoordinate = coordinate(4, 5);
        model.setEntity(onlyDefaultCoordinate, TestEntity.EMPTY);

        Optional<GridCoordinate> result = model.findRandomDefaultCoordinate(new Random(RANDOM_SEED));

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals(onlyDefaultCoordinate, result.orElseThrow())
        );
    }

    @Test
    void testSetEntityWithGridCellUsesCellValues() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(2, 0);

        model.setEntity(new GridCell<>(coordinate, TestEntity.FOOD));

        assertEquals(TestEntity.FOOD, model.getEntity(coordinate));
    }

    @Test
    void testSetEntityToDefaultResetsEntityToDefault() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate coordinate = coordinate(2, 0);
        model.setEntity(coordinate, TestEntity.WALL);

        model.setEntityToDefault(coordinate);

        assertEquals(TestEntity.EMPTY, model.getEntity(coordinate));
    }

    @Test
    void testFillWithEntityWritesAllCells() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        model.fill(TestEntity.FOOD);

        assertAll(
                () -> assertEquals(GRID_CELL_COUNT, model.countEntities(entity -> entity == TestEntity.FOOD)),
                () -> assertEquals(0, model.countEntities(entity -> entity == TestEntity.EMPTY))
        );
    }

    @Test
    void testFillWithSupplierInvokesSupplierPerCell() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        AtomicInteger calls = new AtomicInteger();

        model.fill(() -> ((calls.getAndIncrement() % 2) == 0) ? TestEntity.WALL : TestEntity.FOOD);

        assertAll(
                () -> assertEquals(GRID_CELL_COUNT, calls.get()),
                () -> assertEquals(HALF_GRID_CELL_COUNT, model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(HALF_GRID_CELL_COUNT, model.countEntities(entity -> entity == TestEntity.FOOD))
        );
    }

    @Test
    void testFillWithMapperUsesCoordinate() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);

        model.fill(c -> (c.x() == c.y()) ? TestEntity.WALL : TestEntity.EMPTY);

        assertAll(
                () -> assertEquals(8, model.countEntities(entity -> entity == TestEntity.WALL)),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinate(0, 0))),
                () -> assertEquals(TestEntity.WALL, model.getEntity(coordinate(7, 7))),
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(coordinate(7, 6)))
        );
    }

    @Test
    void testClearResetsAllEntitiesToDefault() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.fill(TestEntity.WALL);

        model.clear();

        assertEquals(GRID_CELL_COUNT, model.countEntities(entity -> entity == TestEntity.EMPTY));
    }

    @Test
    void testSwapInputCellEntitiesWritesInputEntityValues() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
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
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
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
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
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

    private static final class DefaultMethodModel implements WritableGridModel<TestEntity> {

        private final GridStructure structure;
        private final TestEntity defaultEntity;
        private final Map<GridCoordinate, TestEntity> data = new HashMap<>();

        private DefaultMethodModel(GridStructure structure, TestEntity defaultEntity) {
            this.structure = structure;
            this.defaultEntity = defaultEntity;
        }

        @Override
        public GridStructure structure() {
            return structure;
        }

        @Override
        public TestEntity defaultEntity() {
            return defaultEntity;
        }

        @Override
        public TestEntity getEntity(GridCoordinate coordinate) {
            if (!structure.isCoordinateValid(coordinate)) {
                throw new IndexOutOfBoundsException();
            }
            return data.getOrDefault(coordinate, defaultEntity);
        }

        @Override
        public boolean isSparse() {
            return true;
        }

        @Override
        public WritableGridModel<TestEntity> copy() {
            DefaultMethodModel copy = new DefaultMethodModel(structure, defaultEntity);
            copy.data.putAll(data);
            return copy;
        }

        @Override
        public WritableGridModel<TestEntity> copyWithDefaultEntity() {
            return new DefaultMethodModel(structure, defaultEntity);
        }

        @Override
        public void setEntity(GridCoordinate coordinate, TestEntity entity) {
            if (!structure.isCoordinateValid(coordinate)) {
                throw new IndexOutOfBoundsException();
            }
            if (entity == defaultEntity) {
                data.remove(coordinate);
            } else {
                data.put(coordinate, entity);
            }
        }

    }

}
