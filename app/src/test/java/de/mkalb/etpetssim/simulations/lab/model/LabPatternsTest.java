package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.engine.support.GridPatterns;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.shared.LabPattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LabPatternsTest {

    private static final List<GridSize> GRID_SIZES = List.of(
            new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE),
            new GridSize(16, 8),
            new GridSize(40, 24),
            new GridSize(100, 100));

    private static Stream<Arguments> patternsAndGridSizes() {
        return Arrays.stream(LabPattern.values())
                .flatMap(pattern -> GRID_SIZES.stream().map(size -> Arguments.of(pattern, size)));
    }

    @ParameterizedTest
    @MethodSource("patternsAndGridSizes")
    void testCreatePatternFitsIntoGrid(LabPattern labPattern, GridSize size) {
        GridPattern<LabEntity> pattern = LabPatterns.createPattern(labPattern, size);

        assertAll(
                () -> assertFalse(pattern.isEmpty()),
                () -> assertTrue(pattern.isNormalized()),
                () -> assertTrue(pattern.width() <= size.width()),
                () -> assertTrue(pattern.height() <= size.height()),
                () -> assertTrue(pattern.offsetMap().values().stream().allMatch(e -> e == LabEntity.HIGHLIGHTED))
        );
    }

    @ParameterizedTest
    @CsvSource({
            "CROSS,             16, 16, 13, 7, 7",
            "DIAGONALS,         16, 16, 13, 7, 7",
            "DIAGONALS,         16,  8,  5, 3, 3",
            "RECTANGLE_OUTLINE, 16,  8, 20, 8, 4",
            "NESTED_RECTANGLES, 16, 16, 84, 12, 12",
            "ROTATIONS,          8,  8, 12, 6, 6",
            "MIRRORS,            8,  8, 12, 6, 6",
            "PARITY,             8,  8, 12, 7, 7"
    })
    void testCreatePatternScalesToGridSize(LabPattern labPattern, int gridWidth, int gridHeight,
                                           int expectedSize, int expectedWidth, int expectedHeight) {
        GridPattern<LabEntity> pattern = LabPatterns.createPattern(labPattern, new GridSize(gridWidth, gridHeight));

        assertAll(
                () -> assertEquals(expectedSize, pattern.size()),
                () -> assertEquals(expectedWidth, pattern.width()),
                () -> assertEquals(expectedHeight, pattern.height())
        );
    }

    @ParameterizedTest
    @CsvSource({
            "3,  3, 16, 16, 6, 6",
            "7,  5, 16, 16, 4, 4",
            "5,  1, 16,  8, 4, 2",
            "12, 12, 8,  8, 0, 0"
    })
    void testComputeAnchor(int patternWidth, int patternHeight, int gridWidth, int gridHeight,
                           int expectedX, int expectedY) {
        GridPattern<LabEntity> pattern = GridPatterns.rectangleOutline(LabEntity.HIGHLIGHTED, patternWidth,
                patternHeight);

        assertEquals(new GridCoordinate(expectedX, expectedY),
                LabPatterns.computeAnchor(pattern, new GridSize(gridWidth, gridHeight)));
    }

}
