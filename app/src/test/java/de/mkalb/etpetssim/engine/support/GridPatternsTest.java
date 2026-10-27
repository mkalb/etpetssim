package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class GridPatternsTest {

    private static final GridOffset ORIGIN = new GridOffset(0, 0);
    private static final GridOffset OFFSET = new GridOffset(2, -1);

    private static Map<GridOffset, TestEntity> firstAt(int... coordinates) {
        Map<GridOffset, TestEntity> map = new HashMap<>();
        for (int index = 0; index < coordinates.length; index += 2) {
            map.put(new GridOffset(coordinates[index], coordinates[index + 1]), TestEntity.FIRST);
        }
        return map;
    }

    @Test
    void testEmpty() {
        assertTrue(GridPatterns.<TestEntity>empty().offsetMap().isEmpty());
    }

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

    @Test
    void testSingleton() {
        assertEquals(Map.of(OFFSET, TestEntity.FIRST), GridPatterns.singleton(TestEntity.FIRST, OFFSET).offsetMap());
    }

    @Test
    void testCombine() {
        GridPattern<TestEntity> pattern = GridPatterns.combine(
                GridPatterns.of(TestEntity.FIRST, List.of(ORIGIN, OFFSET)),
                GridPatterns.singleton(TestEntity.SECOND, OFFSET));

        assertEquals(Map.of(ORIGIN, TestEntity.FIRST, OFFSET, TestEntity.SECOND), pattern.offsetMap());
    }

    @Test
    void testHorizontalLine() {
        assertEquals(firstAt(0, 0, 1, 0, 2, 0), GridPatterns.horizontalLine(TestEntity.FIRST, 3).offsetMap());
    }

    @Test
    void testHorizontalLineRejectsLengthBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> GridPatterns.horizontalLine(TestEntity.FIRST, 0));
    }

    @Test
    void testVerticalLine() {
        assertEquals(firstAt(0, 0, 0, 1, 0, 2), GridPatterns.verticalLine(TestEntity.FIRST, 3).offsetMap());
    }

    @Test
    void testVerticalLineRejectsLengthBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> GridPatterns.verticalLine(TestEntity.FIRST, 0));
    }

    @Test
    void testRectangleOutline() {
        assertAll(
                () -> assertEquals(firstAt(0, 0, 1, 0, 2, 0, 3, 0, 0, 1, 3, 1, 0, 2, 1, 2, 2, 2, 3, 2),
                        GridPatterns.rectangleOutline(TestEntity.FIRST, 4, 3).offsetMap()),
                () -> assertEquals(firstAt(0, 0, 0, 1),
                        GridPatterns.rectangleOutline(TestEntity.FIRST, 1, 2).offsetMap())
        );
    }

    @Test
    void testRectangleOutlineRejectsSizeBelowOne() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GridPatterns.rectangleOutline(TestEntity.FIRST, 0, 3)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GridPatterns.rectangleOutline(TestEntity.FIRST, 3, 0))
        );
    }

    @Test
    void testCircleOutline() {
        assertAll(
                () -> assertEquals(firstAt(0, 0), GridPatterns.circleOutline(TestEntity.FIRST, 0).offsetMap()),
                () -> assertEquals(firstAt(0, 1, 0, 2, 0, 3, 1, 0, 1, 4, 2, 0, 2, 4, 3, 0, 3, 4, 4, 1, 4, 2, 4, 3),
                        GridPatterns.circleOutline(TestEntity.FIRST, 2).offsetMap())
        );
    }

    @Test
    void testCircleOutlineRejectsNegativeRadius() {
        assertThrows(IllegalArgumentException.class, () -> GridPatterns.circleOutline(TestEntity.FIRST, -1));
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
