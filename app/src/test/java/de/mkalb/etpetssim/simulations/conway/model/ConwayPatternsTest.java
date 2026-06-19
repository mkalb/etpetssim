package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class ConwayPatternsTest {

    private static final int CLASSIC_PATTERN_COUNT = 9;
    private static final List<CellShape> SUPPORTED_CELL_SHAPES = List.of(
            CellShape.TRIANGLE,
            CellShape.SQUARE,
            CellShape.HEXAGON
    );
    private static final List<String> EXPECTED_CHOICE_IDS = List.of(
            "conway.pattern.beehive",
            "conway.pattern.block",
            "conway.pattern.boat",
            "conway.pattern.loaf",
            "conway.pattern.beacon",
            "conway.pattern.blinker",
            "conway.pattern.toad",
            "conway.pattern.glider",
            "conway.pattern.lwss"
    );
    private static final Map<String, PatternExpectation> EXPECTED_PATTERNS = Map.of(
            "conway.pattern.beehive", new PatternExpectation(6, 5, 6),
            "conway.pattern.block", new PatternExpectation(4, 4, 4),
            "conway.pattern.boat", new PatternExpectation(5, 5, 5),
            "conway.pattern.loaf", new PatternExpectation(6, 6, 7),
            "conway.pattern.beacon", new PatternExpectation(6, 6, 6),
            "conway.pattern.blinker", new PatternExpectation(5, 5, 3),
            "conway.pattern.toad", new PatternExpectation(6, 4, 6),
            "conway.pattern.glider", new PatternExpectation(5, 5, 5),
            "conway.pattern.lwss", new PatternExpectation(7, 6, 9)
    );

    private static ConwayConfig createConfig(CellShape cellShape) {
        return new ConwayConfig(
                cellShape,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ConwayConstraints.GRID_WIDTH_DEFAULT,
                ConwayConstraints.GRID_HEIGHT_DEFAULT,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT
        );
    }

    // --- Choice registration tests ---

    private static void assertPattern(GridPattern<ConwayEntity> pattern, int width, int height, long aliveCount) {
        Map<GridOffset, ConwayEntity> offsetMap = pattern.offsetMap();
        long expectedCellCount = (long) width * height;
        long deadCount = offsetMap.values().stream().filter(ConwayEntity.DEAD::equals).count();

        assertAll(
                () -> assertFalse(pattern.isEmpty()),
                () -> assertTrue(pattern.isTopLeftAtOrigin(), "Pattern should be normalized to the origin"),
                () -> assertEquals(width, pattern.width(), "Unexpected pattern width"),
                () -> assertEquals(height, pattern.height(), "Unexpected pattern height"),
                () -> assertEquals(expectedCellCount, pattern.size(), "Pattern should fill the entire rectangle"),
                () -> assertEquals(aliveCount, offsetMap.values().stream().filter(ConwayEntity.ALIVE::equals).count(),
                        "Unexpected number of alive cells"),
                () -> assertEquals(expectedCellCount - aliveCount, deadCount, "Unexpected number of dead cells"),
                () -> assertTrue(hasDeadBorder(offsetMap, width, height), "All edge cells should be dead")
        );
    }

    private static boolean hasDeadBorder(Map<GridOffset, ConwayEntity> offsetMap, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((x != 0) && (y != 0) && (x != (width - 1)) && (y != (height - 1))) {
                    continue;
                }
                if (offsetMap.get(new GridOffset(x, y)) != ConwayEntity.DEAD) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    void testChoicesContainsAllClassicPatternsInOrder() {
        List<ConwayPatternChoice> choices = ConwayPatterns.choices();

        assertAll(
                () -> assertEquals(CLASSIC_PATTERN_COUNT, choices.size(), "All classic patterns should be registered"),
                () -> assertEquals(EXPECTED_CHOICE_IDS, choices.stream().map(ConwayPatternChoice::choiceId).toList()),
                () -> assertEquals(EXPECTED_CHOICE_IDS, choices.stream().map(ConwayPatternChoice::labelKey).toList())
        );
    }

    // --- Pattern structure tests ---

    @Test
    void testAvailableChoicesMatchChoicesForSupportedShapes() {
        List<ConwayPatternChoice> classicChoices = ConwayPatterns.choices();

        assertAll(SUPPORTED_CELL_SHAPES.stream()
                                       .<org.junit.jupiter.api.function.Executable>map(cellShape -> () -> {
                                           ConwayConfig config = createConfig(cellShape);
                                           List<ConwayPatternChoice> availableChoices = ConwayPatterns.availableChoices(config);

                                           assertAll(
                                                   () -> assertEquals(classicChoices, availableChoices,
                                                           "Available choices should match classic choices for " + cellShape),
                                                   () -> assertEquals(EXPECTED_CHOICE_IDS,
                                                           availableChoices.stream().map(ConwayPatternChoice::choiceId).toList(),
                                                           "Choice order should stay stable for " + cellShape)
                                           );
                                       })
                                       .toArray(org.junit.jupiter.api.function.Executable[]::new));
    }

    @Test
    void testChoicePatternsMatchExpectedShapes() {
        List<ConwayPatternChoice> choices = ConwayPatterns.choices();

        assertAll(choices.stream()
                         .<org.junit.jupiter.api.function.Executable>map(choice -> () -> {
                             PatternExpectation expectation = EXPECTED_PATTERNS.get(choice.choiceId());

                             assertAll(
                                     () -> assertNotNull(expectation, "Missing expectation for choiceId " + choice.choiceId()),
                                     () -> assertPattern(
                                             choice.pattern(),
                                             Objects.requireNonNull(expectation).width(),
                                             expectation.height(),
                                             expectation.aliveCount())
                             );
                         })
                         .toArray(org.junit.jupiter.api.function.Executable[]::new));
    }

    @Test
    void testBeehivePatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.beehive(), 6, 5, 6);
    }

    @Test
    void testBlockPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.block(), 4, 4, 4);
    }

    @Test
    void testBoatPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.boat(), 5, 5, 5);
    }

    @Test
    void testLoafPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.loaf(), 6, 6, 7);
    }

    @Test
    void testBeaconPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.beacon(), 6, 6, 6);
    }

    @Test
    void testBlinkerPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.blinker(), 5, 5, 3);
    }

    @Test
    void testToadPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.toad(), 6, 4, 6);
    }

    @Test
    void testGliderPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.glider(), 5, 5, 5);
    }

    @Test
    void testLightweightSpaceshipPatternMatchesExpectedShape() {
        assertPattern(ConwayPatterns.lightweightSpaceship(), 7, 6, 9);
    }

    private record PatternExpectation(int width, int height, long aliveCount) {
    }

}

