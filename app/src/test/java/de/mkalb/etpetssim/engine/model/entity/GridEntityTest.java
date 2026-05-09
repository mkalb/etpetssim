package de.mkalb.etpetssim.engine.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GridEntityTest {

    @Test
    void testIsConstantReturnsTrueForConstantEntity() {
        GridEntity entity = new TestConstantEntity("const");
        assertTrue(GridEntity.isConstant(entity));
    }

    @Test
    void testIsConstantReturnsFalseForNonConstantEntity() {
        GridEntity entity = new TestEntity("dynamic");
        assertFalse(GridEntity.isConstant(entity));
    }

    @Test
    void testToDisplayStringWrapsToString() {
        GridEntity entity = new TestEntity("dynamic");
        assertEquals("[DYNAMIC]", entity.toDisplayString());
    }

    private record TestEntity(String descriptorId) implements GridEntity {

        @Override
        public String toString() {
            return "DYNAMIC";
        }

    }

    private record TestConstantEntity(String descriptorId) implements ConstantGridEntity {

        @Override
        public String toString() {
            return "CONSTANT";
        }

    }

}

