package de.mkalb.etpetssim.engine.model;

import org.junit.jupiter.api.Test;

import static de.mkalb.etpetssim.engine.model.GridModelTestSupport.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class GridCellTest {

    @Test
    void testDescriptorIdDelegatesToEntity() {
        GridCell<TestEntity> cell = new GridCell<>(coordinate(4, 5), TestEntity.WALL);
        assertEquals("wall", cell.descriptorId());
    }

    @Test
    void testToDisplayString() {
        GridCell<TestEntity> cell = new GridCell<>(coordinate(4, 5), TestEntity.WALL);
        assertEquals("(4, 5) [WALL]", cell.toDisplayString());
    }

}

