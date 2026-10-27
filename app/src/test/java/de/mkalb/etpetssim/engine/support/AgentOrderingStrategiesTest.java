package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class AgentOrderingStrategiesTest {

    private static GridCell<TestEntity> cell(int x, int y) {
        return cell(x, y, TestEntity.AGENT);
    }

    private static GridCell<TestEntity> cell(int x, int y, TestEntity entity) {
        return new GridCell<>(new GridCoordinate(x, y), entity);
    }

    @Test
    void testByPosition() {
        List<GridCell<TestEntity>> cells = new ArrayList<>(List.of(cell(2, 1), cell(0, 2), cell(1, 1), cell(3, 0)));

        cells.sort(AgentOrderingStrategies.byPosition());

        assertEquals(List.of(cell(3, 0), cell(1, 1), cell(2, 1), cell(0, 2)), cells);
    }

    @Test
    void testByDescriptorId() {
        List<GridCell<TestEntity>> cells = new ArrayList<>(List.of(
                cell(0, 0, TestEntity.OTHER), cell(2, 1), cell(1, 0, TestEntity.OTHER), cell(1, 1)));

        cells.sort(AgentOrderingStrategies.<TestEntity>byDescriptorId()
                                          .thenComparing(AgentOrderingStrategies.byPosition()));

        assertEquals(List.of(cell(1, 1), cell(2, 1), cell(0, 0, TestEntity.OTHER), cell(1, 0, TestEntity.OTHER)),
                cells);
    }

    private enum TestEntity implements GridEntity {
        AGENT,
        OTHER;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
