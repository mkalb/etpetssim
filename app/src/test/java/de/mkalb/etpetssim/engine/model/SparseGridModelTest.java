package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridModelTestSupport.TestEntity;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.SQUARE_STRUCTURE_8X8;
import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.coordinate;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

final class SparseGridModelTest {

    @Test
    void testFilteredCoordinatesIncludesDefaultCells() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        GridCoordinate wallCoordinate = coordinate(2, 1);
        model.setEntity(wallCoordinate, TestEntity.WALL);

        List<GridCoordinate> coordinates = model.filteredCoordinates(entity -> entity == TestEntity.EMPTY);

        assertAll(
                () -> assertEquals(SQUARE_STRUCTURE_8X8.cellCount() - 1, coordinates.size()),
                () -> assertFalse(coordinates.contains(wallCoordinate))
        );
    }

    @Test
    void testFilteredCoordinatesReturnsOnlyMatchingNonDefaultCoordinates() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(3, 3), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);

        Set<GridCoordinate> filtered = new HashSet<>(model.filteredCoordinates(entity -> entity == TestEntity.WALL));

        assertEquals(Set.of(coordinate(1, 1), coordinate(3, 3)), filtered);
    }

    @Test
    void testFilteredCellsReturnsOnlyMatchingNonDefaultCells() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);
        model.setEntity(coordinate(3, 3), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);

        Set<GridCell<TestEntity>> filtered = new HashSet<>(model.filteredCells(entity -> entity == TestEntity.WALL));

        assertEquals(
                Set.of(
                        new GridCell<>(coordinate(1, 1), TestEntity.WALL),
                        new GridCell<>(coordinate(3, 3), TestEntity.WALL)
                ),
                filtered
        );
    }

}
