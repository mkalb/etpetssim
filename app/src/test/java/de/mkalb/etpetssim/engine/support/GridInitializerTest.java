package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class GridInitializerTest {

    private static final GridStructure STRUCTURE = new GridStructure(
            new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), GridSize.EXTRA_SMALL_SQUARE);

    @Test
    void testAndThenAppliesInitializersInOrder() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(STRUCTURE, TestEntity.EMPTY);
        GridCoordinate shared = new GridCoordinate(1, 1);
        GridCoordinate onlyFirst = new GridCoordinate(0, 0);
        GridInitializer<TestEntity> first = target -> {
            target.setEntity(shared, TestEntity.FILLED);
            target.setEntity(onlyFirst, TestEntity.FILLED);
        };
        GridInitializer<TestEntity> second = target -> target.setEntity(shared, TestEntity.OTHER);

        first.andThen(second).initialize(model);

        assertAll(
                () -> assertEquals(TestEntity.OTHER, model.getEntity(shared)),
                () -> assertEquals(TestEntity.FILLED, model.getEntity(onlyFirst))
        );
    }

    private enum TestEntity implements GridEntity {
        EMPTY,
        FILLED,
        OTHER;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
