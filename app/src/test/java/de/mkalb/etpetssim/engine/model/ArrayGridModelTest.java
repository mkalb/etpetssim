package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class ArrayGridModelTest {

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

}
