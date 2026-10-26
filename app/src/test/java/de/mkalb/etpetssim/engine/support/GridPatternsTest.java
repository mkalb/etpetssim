package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class GridPatternsTest {

    private static final GridOffset ORIGIN = new GridOffset(0, 0);
    private static final GridOffset OFFSET = new GridOffset(2, -1);

    @Test
    void testOfWithMap() {
        Map<GridOffset, TestEntity> map = new HashMap<>(Map.of(ORIGIN, TestEntity.FIRST, OFFSET, TestEntity.SECOND));

        GridPattern<TestEntity> pattern = GridPatterns.of(map);
        map.clear();

        assertEquals(Map.of(ORIGIN, TestEntity.FIRST, OFFSET, TestEntity.SECOND), pattern.offsetMap());
    }

    @Test
    void testOfWithOffsets() {
        List<GridOffset> offsets = new ArrayList<>(List.of(ORIGIN, OFFSET, ORIGIN));

        GridPattern<TestEntity> pattern = GridPatterns.of(TestEntity.FIRST, offsets);
        offsets.clear();

        assertEquals(Map.of(ORIGIN, TestEntity.FIRST, OFFSET, TestEntity.FIRST), pattern.offsetMap());
    }

    private enum TestEntity implements GridEntity {
        FIRST,
        SECOND;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
