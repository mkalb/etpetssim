package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.shared.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class ConwayPatternsTest {

    private static final int PATTERN_COUNT = 15;
    private static final List<CellShape> UNSUPPORTED_PATTERN_CELL_SHAPES = List.of(
            CellShape.TRIANGLE,
            CellShape.HEXAGON
    );
    private static final List<String> EXPECTED_CLASSIC_CHOICE_IDS = List.of(
            "conway.beehive",
            "conway.block",
            "conway.boat",
            "conway.loaf",
            "conway.tub",
            "conway.beacon",
            "conway.blinker",
            "conway.pentadecathlon",
            "conway.pulsar",
            "conway.toad",
            "conway.acorn",
            "conway.rpentomino",
            "conway.glider",
            "conway.lwss"
    );
    private static final List<String> EXPECTED_CHOICE_IDS = List.of(
            "conway.beehive",
            "conway.block",
            "conway.boat",
            "conway.loaf",
            "conway.tub",
            "conway.beacon",
            "conway.blinker",
            "conway.pentadecathlon",
            "conway.pulsar",
            "conway.toad",
            "conway.acorn",
            "conway.rpentomino",
            "conway.highlife.replicator",
            "conway.glider",
            "conway.lwss"
    );
    private static final List<String> EXPECTED_LABEL_KEYS = List.of(
            "conway.pattern.beehive",
            "conway.pattern.block",
            "conway.pattern.boat",
            "conway.pattern.loaf",
            "conway.pattern.tub",
            "conway.pattern.beacon",
            "conway.pattern.blinker",
            "conway.pattern.pentadecathlon",
            "conway.pattern.pulsar",
            "conway.pattern.toad",
            "conway.pattern.acorn",
            "conway.pattern.rpentomino",
            "conway.pattern.highlife.replicator",
            "conway.pattern.glider",
            "conway.pattern.lwss"
    );
    private static final Map<String, PatternExpectation> EXPECTED_PATTERNS = Map.ofEntries(
            Map.entry("conway.beehive", new PatternExpectation(6, 5, 6)),
            Map.entry("conway.block", new PatternExpectation(4, 4, 4)),
            Map.entry("conway.boat", new PatternExpectation(5, 5, 5)),
            Map.entry("conway.loaf", new PatternExpectation(6, 6, 7)),
            Map.entry("conway.tub", new PatternExpectation(5, 5, 4)),
            Map.entry("conway.beacon", new PatternExpectation(6, 6, 6)),
            Map.entry("conway.blinker", new PatternExpectation(5, 5, 3)),
            Map.entry("conway.pentadecathlon", new PatternExpectation(12, 5, 12)),
            Map.entry("conway.pulsar", new PatternExpectation(15, 15, 48)),
            Map.entry("conway.toad", new PatternExpectation(6, 4, 6)),
            Map.entry("conway.acorn", new PatternExpectation(9, 5, 7)),
            Map.entry("conway.rpentomino", new PatternExpectation(5, 5, 5)),
            Map.entry("conway.highlife.replicator", new PatternExpectation(7, 7, 12)),
            Map.entry("conway.glider", new PatternExpectation(5, 5, 5)),
            Map.entry("conway.lwss", new PatternExpectation(7, 6, 9))
    );

    private static ConwayConfig createConfig(CellShape cellShape) {
        return createConfig(cellShape, ConwayConstraints.TRANSITION_RULES_DEFAULT);
    }

    private static ConwayConfig createConfig(CellShape cellShape, ConwayTransitionRules transitionRules) {
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
                transitionRules
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
                () -> assertEquals(PATTERN_COUNT, choices.size(), "All patterns should be registered"),
                () -> assertEquals(EXPECTED_CHOICE_IDS, choices.stream().map(ConwayPatternChoice::choiceId).toList()),
                () -> assertEquals(EXPECTED_LABEL_KEYS, choices.stream().map(ConwayPatternChoice::labelKey).toList()),
                () -> assertTrue(choices.stream().noneMatch(choice -> choice.choiceId().equals(choice.labelKey())),
                        "Pattern choice ids must stay separate from localization keys")
        );
    }

    // --- Pattern structure tests ---

    @Test
    void testAvailableChoicesMatchChoicesForClassicSquareConfig() {
        ConwayConfig config = createConfig(CellShape.SQUARE);
        List<ConwayPatternChoice> availableChoices = ConwayPatterns.availableChoices(config);

        assertEquals(EXPECTED_CLASSIC_CHOICE_IDS,
                availableChoices.stream().map(ConwayPatternChoice::choiceId).toList(),
                "Choice order should stay stable for the classic square config");
    }

    @Test
    void testAvailableChoicesRejectUnsupportedCellShapes() {
        assertAll(UNSUPPORTED_PATTERN_CELL_SHAPES.stream()
                                                 .<org.junit.jupiter.api.function.Executable>map(cellShape -> () -> {
                                                     ConwayConfig config = createConfig(cellShape);
                                                     List<ConwayPatternChoice> availableChoices = ConwayPatterns.availableChoices(config);

                                                     assertTrue(availableChoices.isEmpty(),
                                                             "Classic patterns should not be available for " + cellShape);
                                                 })
                                                 .toArray(org.junit.jupiter.api.function.Executable[]::new));
    }

    @Test
    void testAvailableChoicesRejectNonClassicTransitionRules() {
        ConwayConfig config = createConfig(
                CellShape.SQUARE,
                ConwayTransitionRules.of(Set.of(2, 3), Set.of(3, 4)));

        assertTrue(ConwayPatterns.availableChoices(config).isEmpty(),
                "Classic patterns should not be available for non-classic transition rules");
    }

    @Test
    void testAvailableChoicesIncludeHighLifeReplicatorForHighLifeRules() {
        ConwayConfig config = createConfig(
                CellShape.SQUARE,
                ConwayTransitionRules.of(ConwayPresetSquare.HIGHLIFE.toString()));

        List<ConwayPatternChoice> availableChoices = ConwayPatterns.availableChoices(config);

        assertEquals(List.of("conway.highlife.replicator"),
                availableChoices.stream().map(ConwayPatternChoice::choiceId).toList());
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
    void testTubPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.tub(), 5, 5, 4);
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
    void testPentadecathlonPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.pentadecathlon(), 12, 5, 12);
    }

    @Test
    void testPulsarPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.pulsar(), 15, 15, 48);
    }

    @Test
    void testToadPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.toad(), 6, 4, 6);
    }

    @Test
    void testAcornPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.acorn(), 9, 5, 7);
    }

    @Test
    void testRPentominoPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.rPentomino(), 5, 5, 5);
    }

    @Test
    void testHighLifeReplicatorPatternHasDeadBorderAndExpectedDimensions() {
        assertPattern(ConwayPatterns.highlifeReplicator(), 7, 7, 12);
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
