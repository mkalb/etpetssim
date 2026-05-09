package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

final class WritableGridModelDefaultMethodsTest {

    private static final int RANDOM_SEED = 123;

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

    @Test
    void testGetEntityAsOptionalAndRandomDefaultCoordinate() {
        DefaultMethodModel model = new DefaultMethodModel(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate occupied = coordinate(0, 0);
        model.setEntity(occupied, TestEntity.WALL);

        assertEquals(Optional.of(TestEntity.WALL), model.getEntityAsOptional(occupied));
        assertTrue(model.getEntityAsOptional(coordinate(9, 9)).isEmpty());

        Optional<GridCoordinate> randomDefault = model.randomDefaultCoordinate(new Random(RANDOM_SEED));
        assertTrue(randomDefault.isPresent());
        assertNotEquals(occupied, randomDefault.orElseThrow());
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
