package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridModelTestSupport.TestEntity;
import org.junit.jupiter.api.Test;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.coordinate;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class GridCellViewTest {

    @Test
    void testDescriptorId() {
        GridCellView<TestEntity> cell = new TestCellView(coordinate(4, 5), TestEntity.WALL);
        assertEquals("wall", cell.descriptorId());
    }

    @Test
    void testToDisplayString() {
        GridCellView<TestEntity> cell = new TestCellView(coordinate(4, 5), TestEntity.WALL);
        assertEquals("(4, 5) [WALL]", cell.toDisplayString());
    }

    @SuppressWarnings("SameParameterValue")
    private record TestCellView(GridCoordinate coordinate, TestEntity entity) implements GridCellView<TestEntity> {
    }

}
