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
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private static void assertUnmodifiableOffsetMap(Map<GridOffset, TestEntity> expected,
                                                    GridPattern<TestEntity> pattern) {
        assertAll(
                () -> assertEquals(expected, pattern.offsetMap()),
                () -> assertThrows(UnsupportedOperationException.class, () -> pattern.offsetMap().clear())
        );
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
                () -> assertEquals(2, createPattern().shift(new GridOffset(5, 7)).width()),
                () -> assertEquals(0, createEmptyPattern().width())
        );
    }

    @Test
    void testHeight() {
        assertAll(
                () -> assertEquals(3, createPattern().height()),
                () -> assertEquals(3, createPattern().shift(new GridOffset(5, 7)).height()),
                () -> assertEquals(0, createEmptyPattern().height())
        );
    }

    @Test
    void testIsNormalized() {
        assertAll(
                () -> assertTrue(createPattern().isNormalized()),
                () -> assertFalse(createPattern().shift(new GridOffset(1, 0)).isNormalized()),
                () -> assertFalse(createPattern().shift(new GridOffset(0, -1)).isNormalized()),
                () -> assertTrue(createEmptyPattern().isNormalized())
        );
    }

    @Test
    void testShift() {
        assertUnmodifiableOffsetMap(createOffsetMap(3, -1, 3, 0, 3, 1, 4, 1),
                createPattern().shift(new GridOffset(3, -1)));
    }

    @Test
    void testNormalize() {
        GridPattern<TestEntity> shifted = createPattern().shift(new GridOffset(5, -7));

        assertAll(
                () -> assertEquals(createPattern().offsetMap(), shifted.normalize().offsetMap()),
                () -> assertTrue(shifted.normalize().isNormalized())
        );
    }

    @Test
    void testNormalizeReturnsSameInstanceWhenNormalized() {
        GridPattern<TestEntity> pattern = createPattern();
        assertSame(pattern, pattern.normalize());
    }

    @Test
    void testFlipX() {
        assertUnmodifiableOffsetMap(createOffsetMap(1, 0, 1, 1, 1, 2, 0, 2), createPattern().flipX());
    }

    @Test
    void testFlipXKeepsBoundingBox() {
        GridPattern<TestEntity> shifted = createPattern().shift(new GridOffset(4, 9));
        assertEquals(createPattern().flipX().shift(new GridOffset(4, 9)).offsetMap(), shifted.flipX().offsetMap());
    }

    @Test
    void testFlipY() {
        assertUnmodifiableOffsetMap(createOffsetMap(0, 2, 0, 1, 0, 0, 1, 0), createPattern().flipY());
    }

    @Test
    void testFlipYKeepsBoundingBox() {
        GridPattern<TestEntity> shifted = createPattern().shift(new GridOffset(4, 9));
        assertEquals(createPattern().flipY().shift(new GridOffset(4, 9)).offsetMap(), shifted.flipY().offsetMap());
    }

    @Test
    void testRotate90() {
        assertUnmodifiableOffsetMap(createOffsetMap(2, 0, 1, 0, 0, 0, 0, 1), createPattern().rotate90());
    }

    @Test
    void testRotate90FourTimesRestoresPattern() {
        GridPattern<TestEntity> rotated = createPattern().rotate90().rotate90().rotate90().rotate90();
        assertEquals(createPattern().offsetMap(), rotated.offsetMap());
    }

    @Test
    void testRotate90NormalizesShiftedPattern() {
        GridPattern<TestEntity> shifted = createPattern().shift(new GridOffset(4, 9));
        assertEquals(createPattern().rotate90().offsetMap(), shifted.rotate90().offsetMap());
    }

    @Test
    void testRotate180() {
        assertUnmodifiableOffsetMap(createOffsetMap(1, 2, 1, 1, 1, 0, 0, 0), createPattern().rotate180());
    }

    @Test
    void testRotate180NormalizesShiftedPattern() {
        GridPattern<TestEntity> shifted = createPattern().shift(new GridOffset(4, 9));
        assertEquals(createPattern().rotate180().offsetMap(), shifted.rotate180().offsetMap());
    }

    @Test
    void testRotate270() {
        assertUnmodifiableOffsetMap(createOffsetMap(0, 1, 1, 1, 2, 1, 2, 0), createPattern().rotate270());
    }

    @Test
    void testRotate270NormalizesShiftedPattern() {
        GridPattern<TestEntity> shifted = createPattern().shift(new GridOffset(4, 9));
        assertEquals(createPattern().rotate270().offsetMap(), shifted.rotate270().offsetMap());
    }

    @Test
    void testMapEntities() {
        GridPattern<TestEntity> mapped = createPattern().mapEntities(
                entity -> (entity == TestEntity.FILLED) ? TestEntity.EMPTY : entity);

        assertUnmodifiableOffsetMap(Map.of(
                new GridOffset(0, 0), TestEntity.OTHER,
                new GridOffset(0, 1), TestEntity.EMPTY,
                new GridOffset(0, 2), TestEntity.EMPTY,
                new GridOffset(1, 2), TestEntity.EMPTY), mapped);
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
