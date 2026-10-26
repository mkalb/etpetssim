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
        return new GridCell<>(new GridCoordinate(x, y), TestEntity.AGENT);
    }

    @Test
    void testByPosition() {
        List<GridCell<TestEntity>> cells = new ArrayList<>(List.of(cell(2, 1), cell(0, 2), cell(1, 1), cell(3, 0)));

        cells.sort(AgentOrderingStrategies.byPosition());

        assertEquals(List.of(cell(3, 0), cell(1, 1), cell(2, 1), cell(0, 2)), cells);
    }

    private enum TestEntity implements GridEntity {
        AGENT;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
