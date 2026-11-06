package de.mkalb.etpetssim.engine.model.entity;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class GridEntityTest {

    @Test
    void testIsConstant() {
        assertAll(
                () -> assertTrue(GridEntity.isConstant(new TestConstantEntity("const"))),
                () -> assertFalse(GridEntity.isConstant(new TestEntity("dynamic")))
        );
    }

    @Test
    void testToDisplayString() {
        assertEquals("[DYNAMIC]", new TestEntity("dynamic").toDisplayString());
    }

    private record TestEntity(String descriptorId) implements GridEntity {

        @NonNull
        @Override
        public String toString() {
            return "DYNAMIC";
        }

    }

    private record TestConstantEntity(String descriptorId) implements ConstantGridEntity {
    }

}
