package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridModelTestSupport.TestEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.SQUARE_STRUCTURE_8X8;
import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.coordinate;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class ArrayGridModelTest {

    @Test
    void testFilteredCoordinatesIsInRowMajorOrder() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(3, 3), TestEntity.WALL);
        model.setEntity(coordinate(2, 2), TestEntity.FOOD);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);

        List<GridCoordinate> filtered = model.filteredCoordinates(entity -> entity == TestEntity.WALL);

        assertEquals(List.of(coordinate(1, 1), coordinate(3, 3)), filtered);
    }

    @Test
    void testFilteredCellsIsInRowMajorOrder() {
        ArrayGridModel<TestEntity> model = new ArrayGridModel<>(SQUARE_STRUCTURE_8X8, TestEntity.EMPTY);
        model.setEntity(coordinate(3, 3), TestEntity.WALL);
        model.setEntity(coordinate(1, 1), TestEntity.WALL);

        List<GridCell<TestEntity>> filtered = model.filteredCells(entity -> entity == TestEntity.WALL);

        assertEquals(
                List.of(
                        new GridCell<>(coordinate(1, 1), TestEntity.WALL),
                        new GridCell<>(coordinate(3, 3), TestEntity.WALL)
                ),
                filtered
        );
    }

}
