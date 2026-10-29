package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class GridPatternTest {

    private static GridPattern<TestEntity> createPattern() {
        Map<GridOffset, TestEntity> map = Map.of(
                new GridOffset(0, 0), TestEntity.OTHER,
                new GridOffset(0, 1), TestEntity.FILLED,
                new GridOffset(0, 2), TestEntity.FILLED,
                new GridOffset(1, 2), TestEntity.FILLED);
        return () -> map;
    }

    private static GridPattern<TestEntity> createEmptyPattern() {
        return Map::of;
    }

    private static Map<GridOffset, TestEntity> createOffsetMap(int otherX, int otherY, int... filledXY) {
        Map<GridOffset, TestEntity> map = new HashMap<>();
        map.put(new GridOffset(otherX, otherY), TestEntity.OTHER);
        for (int i = 0; i < filledXY.length; i += 2) {
            map.put(new GridOffset(filledXY[i], filledXY[i + 1]), TestEntity.FILLED);
        }
        return map;
    }

    @Test
    void testSize() {
        assertAll(
                () -> assertEquals(4, createPattern().size()),
                () -> assertEquals(0, createEmptyPattern().size())
        );
    }

    @Test
    void testIsEmpty() {
        assertAll(
                () -> assertFalse(createPattern().isEmpty()),
                () -> assertTrue(createEmptyPattern().isEmpty())
        );
    }

    @Test
    void testWidth() {
        assertAll(
                () -> assertEquals(2, createPattern().width()),
                () -> assertEquals(2, createPattern().shifted(new GridOffset(5, 7)).width()),
                () -> assertEquals(0, createEmptyPattern().width())
        );
    }

    @Test
    void testHeight() {
        assertAll(
                () -> assertEquals(3, createPattern().height()),
                () -> assertEquals(3, createPattern().shifted(new GridOffset(5, 7)).height()),
                () -> assertEquals(0, createEmptyPattern().height())
        );
    }

    @Test
    void testShifted() {
        assertEquals(createOffsetMap(3, -1, 3, 0, 3, 1, 4, 1), createPattern().shifted(new GridOffset(3, -1)).offsetMap());
    }

    @Test
    void testIsTopLeftAtOrigin() {
        assertAll(
                () -> assertTrue(createPattern().isTopLeftAtOrigin()),
                () -> assertFalse(createPattern().shifted(new GridOffset(1, 0)).isTopLeftAtOrigin()),
                () -> assertFalse(createPattern().shifted(new GridOffset(0, -1)).isTopLeftAtOrigin()),
                () -> assertTrue(createEmptyPattern().isTopLeftAtOrigin())
        );
    }

    @Test
    void testNormalized() {
        GridPattern<TestEntity> shifted = createPattern().shifted(new GridOffset(5, -7));

        assertAll(
                () -> assertEquals(createPattern().offsetMap(), shifted.normalized().offsetMap()),
                () -> assertTrue(shifted.normalized().isTopLeftAtOrigin())
        );
    }

    @Test
    void testNormalizedReturnsSameInstanceWhenAlreadyNormalized() {
        GridPattern<TestEntity> pattern = createPattern();
        assertSame(pattern, pattern.normalized());
    }

    @Test
    void testFlipX() {
        assertEquals(createOffsetMap(1, 0, 1, 1, 1, 2, 0, 2), createPattern().flipX().offsetMap());
    }

    @Test
    void testFlipY() {
        assertEquals(createOffsetMap(0, 2, 0, 1, 0, 0, 1, 0), createPattern().flipY().offsetMap());
    }

    @Test
    void testRotate90() {
        assertEquals(createOffsetMap(2, 0, 1, 0, 0, 0, 0, 1), createPattern().rotate90().offsetMap());
    }

    @Test
    void testRotate90FourTimesRestoresPattern() {
        GridPattern<TestEntity> rotated = createPattern().rotate90().rotate90().rotate90().rotate90();
        assertEquals(createPattern().offsetMap(), rotated.offsetMap());
    }

    @Test
    void testRotate90NormalizesShiftedPattern() {
        GridPattern<TestEntity> shifted = createPattern().shifted(new GridOffset(4, 9));
        assertEquals(createPattern().rotate90().offsetMap(), shifted.rotate90().offsetMap());
    }

    @Test
    void testRotate180() {
        assertEquals(createOffsetMap(1, 2, 1, 1, 1, 0, 0, 0), createPattern().rotate180().offsetMap());
    }

    @Test
    void testRotate180NormalizesShiftedPattern() {
        GridPattern<TestEntity> shifted = createPattern().shifted(new GridOffset(4, 9));
        assertEquals(createPattern().rotate180().offsetMap(), shifted.rotate180().offsetMap());
    }

    @Test
    void testRotate270() {
        assertEquals(createOffsetMap(0, 1, 1, 1, 2, 1, 2, 0), createPattern().rotate270().offsetMap());
    }

    @Test
    void testRotate270NormalizesShiftedPattern() {
        GridPattern<TestEntity> shifted = createPattern().shifted(new GridOffset(4, 9));
        assertEquals(createPattern().rotate270().offsetMap(), shifted.rotate270().offsetMap());
    }

    @Test
    void testMapValues() {
        GridPattern<TestEntity> mapped = createPattern().mapValues(
                entity -> (entity == TestEntity.FILLED) ? TestEntity.EMPTY : entity);

        assertAll(
                () -> assertEquals(createPattern().offsetMap().keySet(), mapped.offsetMap().keySet()),
                () -> assertEquals(TestEntity.OTHER, mapped.offsetMap().get(new GridOffset(0, 0))),
                () -> assertEquals(TestEntity.EMPTY, mapped.offsetMap().get(new GridOffset(0, 1))),
                () -> assertEquals(TestEntity.EMPTY, mapped.offsetMap().get(new GridOffset(1, 2)))
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
