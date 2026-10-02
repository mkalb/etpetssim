package de.mkalb.etpetssim.engine.model.entity;

import org.jspecify.annotations.NonNull;
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

    @SuppressWarnings("SameParameterValue")
    private record TestEntity(String descriptorId) implements GridEntity {

        @NonNull
        @Override
        public String toString() {
            return "DYNAMIC";
        }

    }

    @SuppressWarnings("SameParameterValue")
    private record TestConstantEntity(String descriptorId) implements ConstantGridEntity {

        @NonNull
        @Override
        public String toString() {
            return "CONSTANT";
        }

    }

}

