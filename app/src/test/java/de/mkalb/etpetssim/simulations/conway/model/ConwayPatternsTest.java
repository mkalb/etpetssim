package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.shared.*;
import de.mkalb.etpetssim.testsupport.LocalizationTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

final class ConwayPatternsTest {

    private static final String HIGHLIFE_REPLICATOR_ID = "conway.highlife.replicator";
    private static final List<PatternExpectation> EXPECTED_PATTERNS = List.of(
            new PatternExpectation("conway.beehive", 6, 5, 6),
            new PatternExpectation("conway.block", 4, 4, 4),
            new PatternExpectation("conway.boat", 5, 5, 5),
            new PatternExpectation("conway.loaf", 6, 6, 7),
            new PatternExpectation("conway.tub", 5, 5, 4),
            new PatternExpectation("conway.beacon", 6, 6, 6),
            new PatternExpectation("conway.blinker", 5, 5, 3),
            new PatternExpectation("conway.pentadecathlon", 12, 5, 12),
            new PatternExpectation("conway.pulsar", 15, 15, 48),
            new PatternExpectation("conway.toad", 6, 4, 6),
            new PatternExpectation("conway.acorn", 9, 5, 7),
            new PatternExpectation("conway.rpentomino", 5, 5, 5),
            new PatternExpectation(HIGHLIFE_REPLICATOR_ID, 7, 7, 12),
            new PatternExpectation("conway.glider", 5, 5, 5),
            new PatternExpectation("conway.lwss", 7, 6, 9)
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

    private static List<String> choiceIds(List<ConwayPatternChoice> choices) {
        return choices.stream().map(ConwayPatternChoice::choiceId).toList();
    }

    private static void assertPattern(GridPattern<ConwayEntity> pattern, PatternExpectation expectation) {
        Map<GridOffset, ConwayEntity> offsetMap = pattern.offsetMap();
        long expectedCellCount = (long) expectation.width() * expectation.height();
        long aliveCount = offsetMap.values().stream().filter(ConwayEntity.ALIVE::equals).count();
        long deadCount = offsetMap.values().stream().filter(ConwayEntity.DEAD::equals).count();

        assertAll(expectation.choiceId(),
                () -> assertTrue(pattern.isTopLeftAtOrigin(), "Pattern should be normalized to the origin"),
                () -> assertEquals(expectation.width(), pattern.width(), "Unexpected pattern width"),
                () -> assertEquals(expectation.height(), pattern.height(), "Unexpected pattern height"),
                () -> assertEquals(expectedCellCount, pattern.size(), "Pattern should fill the entire rectangle"),
                () -> assertEquals(expectation.aliveCount(), aliveCount, "Unexpected number of alive cells"),
                () -> assertEquals(expectedCellCount - expectation.aliveCount(), deadCount,
                        "Unexpected number of dead cells"),
                () -> assertTrue(hasDeadBorder(offsetMap, expectation.width(), expectation.height()),
                        "All edge cells should be dead"),
                () -> assertThrows(UnsupportedOperationException.class, offsetMap::clear)
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
    void testChoices() {
        List<ConwayPatternChoice> choices = ConwayPatterns.choices();
        List<String> labelKeys = choices.stream().map(ConwayPatternChoice::labelKey).toList();

        assertAll(
                () -> assertEquals(EXPECTED_PATTERNS.stream().map(PatternExpectation::choiceId).toList(),
                        choiceIds(choices)),
                () -> LocalizationTestSupport.assertKeysInAllBundles(labelKeys),
                () -> assertTrue(choices.stream().noneMatch(choice -> choice.choiceId().equals(choice.labelKey())),
                        "Pattern choice ids must stay separate from localization keys")
        );
    }

    @Test
    void testChoicesPatternsMatchExpectedShapes() {
        List<ConwayPatternChoice> choices = ConwayPatterns.choices();

        assertEquals(EXPECTED_PATTERNS.size(), choices.size());
        assertAll(IntStream.range(0, choices.size())
                           .mapToObj(index -> () -> assertPattern(
                                   choices.get(index).pattern(),
                                   EXPECTED_PATTERNS.get(index))));
    }

    @Test
    void testAvailableChoices() {
        List<String> expectedChoiceIds = EXPECTED_PATTERNS.stream()
                                                          .map(PatternExpectation::choiceId)
                                                          .filter(choiceId -> !choiceId.equals(HIGHLIFE_REPLICATOR_ID))
                                                          .toList();

        assertEquals(expectedChoiceIds, choiceIds(ConwayPatterns.availableChoices(createConfig(CellShape.SQUARE))));
    }

    @ParameterizedTest
    @EnumSource(value = CellShape.class, names = {"TRIANGLE", "HEXAGON"})
    void testAvailableChoicesReturnsEmptyForUnsupportedCellShape(CellShape cellShape) {
        assertTrue(ConwayPatterns.availableChoices(createConfig(cellShape)).isEmpty());
    }

    @Test
    void testAvailableChoicesReturnsEmptyForOtherTransitionRules() {
        ConwayConfig config = createConfig(
                CellShape.SQUARE,
                ConwayTransitionRules.of(Set.of(2, 3), Set.of(3, 4)));

        assertTrue(ConwayPatterns.availableChoices(config).isEmpty());
    }

    @Test
    void testAvailableChoicesReturnsReplicatorForHighLifeRules() {
        ConwayConfig config = createConfig(
                CellShape.SQUARE,
                ConwayTransitionRules.parse(ConwayRulePresetSquare.HIGHLIFE.ruleString()));

        assertEquals(List.of(HIGHLIFE_REPLICATOR_ID), choiceIds(ConwayPatterns.availableChoices(config)));
    }

    private record PatternExpectation(String choiceId, int width, int height, long aliveCount) {
    }

}
