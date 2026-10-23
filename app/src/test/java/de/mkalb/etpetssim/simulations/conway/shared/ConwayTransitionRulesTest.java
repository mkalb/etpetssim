package de.mkalb.etpetssim.simulations.conway.shared;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class ConwayTransitionRulesTest {

    @Test
    void testRecord() {
        SortedSet<Integer> surviveCounts = new TreeSet<>(Comparator.reverseOrder());
        surviveCounts.addAll(List.of(
                ConwayTransitionRules.MAX_NEIGHBOR_COUNT, 3, ConwayTransitionRules.MIN_NEIGHBOR_COUNT));
        SortedSet<Integer> birthCounts = new TreeSet<>();

        ConwayTransitionRules rules = new ConwayTransitionRules(surviveCounts, birthCounts);
        surviveCounts.add(5);

        assertAll(
                () -> assertEquals(List.of(0, 3, 9), new ArrayList<>(rules.surviveCounts())),
                () -> assertNull(rules.surviveCounts().comparator()),
                () -> assertTrue(rules.birthCounts().isEmpty()),
                () -> assertThrows(UnsupportedOperationException.class, () -> rules.surviveCounts().add(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> rules.birthCounts().add(1))
        );
    }

    @Test
    void testConstructorRejectsSurviveCountBelowMin() {
        SortedSet<Integer> invalid = new TreeSet<>(List.of(ConwayTransitionRules.MIN_NEIGHBOR_COUNT - 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ConwayTransitionRules(invalid, new TreeSet<>()));
    }

    @Test
    void testConstructorRejectsSurviveCountAboveMax() {
        SortedSet<Integer> invalid = new TreeSet<>(List.of(ConwayTransitionRules.MAX_NEIGHBOR_COUNT + 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ConwayTransitionRules(invalid, new TreeSet<>()));
    }

    @Test
    void testConstructorRejectsBirthCountBelowMin() {
        SortedSet<Integer> invalid = new TreeSet<>(List.of(ConwayTransitionRules.MIN_NEIGHBOR_COUNT - 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ConwayTransitionRules(new TreeSet<>(), invalid));
    }

    @Test
    void testConstructorRejectsBirthCountAboveMax() {
        SortedSet<Integer> invalid = new TreeSet<>(List.of(ConwayTransitionRules.MAX_NEIGHBOR_COUNT + 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ConwayTransitionRules(new TreeSet<>(), invalid));
    }

    @Test
    void testOf() {
        ConwayTransitionRules rules = ConwayTransitionRules.of(List.of(3, 2, 5, 3), List.of(6, 1));
        assertAll(
                () -> assertEquals(List.of(2, 3, 5), new ArrayList<>(rules.surviveCounts())),
                () -> assertEquals(List.of(1, 6), new ArrayList<>(rules.birthCounts()))
        );
    }

    @Test
    void testParse() {
        ConwayTransitionRules conwayLife = ConwayTransitionRules.parse("23/3");
        ConwayTransitionRules multiDigit = ConwayTransitionRules.parse("1257/1357");
        assertAll(
                () -> assertEquals(Set.of(2, 3), conwayLife.surviveCounts()),
                () -> assertEquals(Set.of(3), conwayLife.birthCounts()),
                () -> assertEquals(Set.of(1, 2, 5, 7), multiDigit.surviveCounts()),
                () -> assertEquals(Set.of(1, 3, 5, 7), multiDigit.birthCounts())
        );
    }

    @Test
    void testParseReturnsEmptySetsForEmptyParts() {
        ConwayTransitionRules rules = ConwayTransitionRules.parse("/");
        assertAll(
                () -> assertTrue(rules.surviveCounts().isEmpty()),
                () -> assertTrue(rules.birthCounts().isEmpty())
        );
    }

    @Test
    void testParseIgnoresDuplicateDigits() {
        assertEquals(ConwayTransitionRules.parse("23/3"), ConwayTransitionRules.parse("223/33"));
    }

    @Test
    void testParseIgnoresNonDigitCharacters() {
        assertEquals(ConwayTransitionRules.parse("23/3"), ConwayTransitionRules.parse("2 3/3!"));
    }

    @Test
    void testParseRejectsMissingSlash() {
        assertThrows(IllegalArgumentException.class, () -> ConwayTransitionRules.parse("23"));
    }

    @Test
    void testParseRejectsMultipleSlashes() {
        assertThrows(IllegalArgumentException.class, () -> ConwayTransitionRules.parse("2/3/4"));
    }

    @Test
    void testShouldSurvive() {
        ConwayTransitionRules rules = ConwayTransitionRules.parse("23/3");
        ConwayTransitionRules emptySurvive = ConwayTransitionRules.parse("/3");
        assertAll(
                () -> assertTrue(rules.shouldSurvive(2)),
                () -> assertTrue(rules.shouldSurvive(3)),
                () -> assertFalse(rules.shouldSurvive(0)),
                () -> assertFalse(rules.shouldSurvive(1)),
                () -> assertFalse(rules.shouldSurvive(4)),
                () -> assertFalse(emptySurvive.shouldSurvive(2))
        );
    }

    @Test
    void testShouldBeBorn() {
        ConwayTransitionRules rules = ConwayTransitionRules.parse("23/3");
        ConwayTransitionRules emptyBirth = ConwayTransitionRules.parse("23/");
        assertAll(
                () -> assertTrue(rules.shouldBeBorn(3)),
                () -> assertFalse(rules.shouldBeBorn(0)),
                () -> assertFalse(rules.shouldBeBorn(2)),
                () -> assertFalse(rules.shouldBeBorn(4)),
                () -> assertFalse(emptyBirth.shouldBeBorn(3))
        );
    }

    @Test
    void testToDisplayString() {
        assertAll(
                () -> assertEquals("23/3", ConwayTransitionRules.parse("23/3").toDisplayString()),
                () -> assertEquals("235/36",
                        ConwayTransitionRules.of(List.of(5, 2, 3), List.of(6, 3)).toDisplayString()),
                () -> assertEquals("/", ConwayTransitionRules.of(List.of(), List.of()).toDisplayString())
        );
    }

    @Test
    void testToDisplayStringIsParsable() {
        String original = "1257/1357";
        assertEquals(original, ConwayTransitionRules.parse(original).toDisplayString());
    }

}

