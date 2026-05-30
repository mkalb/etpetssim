package de.mkalb.etpetssim.simulations.conway.shared;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class ConwayTransitionRulesTest {

    // --- Construction via collections ---

    @Test
    void testOfCollectionsCreatesCorrectRules() {
        ConwayTransitionRules rules = ConwayTransitionRules.of(List.of(2, 3), List.of(3));
        assertAll(
                () -> assertEquals(new TreeSet<>(List.of(2, 3)), rules.surviveCounts()),
                () -> assertEquals(new TreeSet<>(List.of(3)), rules.birthCounts())
        );
    }

    @Test
    void testConstructorWithEmptySetsIsAllowed() {
        ConwayTransitionRules rules = new ConwayTransitionRules(new TreeSet<>(), new TreeSet<>());
        assertAll(
                () -> assertTrue(rules.surviveCounts().isEmpty()),
                () -> assertTrue(rules.birthCounts().isEmpty())
        );
    }

    @Test
    void testConstructorStoresSurvivaAndBirthCountsInSortedOrder() {
        ConwayTransitionRules rules = ConwayTransitionRules.of(List.of(3, 2, 5), List.of(6, 1));
        assertAll(
                () -> assertEquals(List.of(2, 3, 5), new ArrayList<>(rules.surviveCounts())),
                () -> assertEquals(List.of(1, 6), new ArrayList<>(rules.birthCounts()))
        );
    }

    @Test
    void testConstructorRejectsSurvivaCountBelowMin() {
        SortedSet<Integer> invalid = new TreeSet<>(List.of(ConwayTransitionRules.MIN_NEIGHBOR_COUNT - 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ConwayTransitionRules(invalid, new TreeSet<>()));
    }

    @Test
    void testConstructorRejectsSurvivaCountAboveMax() {
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
    void testConstructorAcceptsBoundaryValues() {
        SortedSet<Integer> boundary = new TreeSet<>(
                List.of(ConwayTransitionRules.MIN_NEIGHBOR_COUNT, ConwayTransitionRules.MAX_NEIGHBOR_COUNT));
        assertDoesNotThrow(() -> new ConwayTransitionRules(boundary, boundary));
    }

    @Test
    void testSurvivaCountsIsUnmodifiable() {
        ConwayTransitionRules rules = ConwayTransitionRules.of(List.of(2, 3), List.of(3));
        assertThrows(UnsupportedOperationException.class, () -> rules.surviveCounts().add(1));
    }

    @Test
    void testBirthCountsIsUnmodifiable() {
        ConwayTransitionRules rules = ConwayTransitionRules.of(List.of(2, 3), List.of(3));
        assertThrows(UnsupportedOperationException.class, () -> rules.birthCounts().add(1));
    }

    // --- Construction via display string ---

    @Test
    void testOfDisplayStringParsesConwayLifeRules() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("23/3");
        assertAll(
                () -> assertEquals(new TreeSet<>(List.of(2, 3)), rules.surviveCounts()),
                () -> assertEquals(new TreeSet<>(List.of(3)), rules.birthCounts())
        );
    }

    @Test
    void testOfDisplayStringParsesMultiDigitRules() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("1257/1357");
        assertAll(
                () -> assertEquals(new TreeSet<>(List.of(1, 2, 5, 7)), rules.surviveCounts()),
                () -> assertEquals(new TreeSet<>(List.of(1, 3, 5, 7)), rules.birthCounts())
        );
    }

    @Test
    void testOfDisplayStringParsesEmptyParts() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("/");
        assertAll(
                () -> assertTrue(rules.surviveCounts().isEmpty()),
                () -> assertTrue(rules.birthCounts().isEmpty())
        );
    }

    @Test
    void testOfDisplayStringDeduplicatesDigits() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("223/33");
        assertAll(
                () -> assertEquals(new TreeSet<>(List.of(2, 3)), rules.surviveCounts()),
                () -> assertEquals(new TreeSet<>(List.of(3)), rules.birthCounts())
        );
    }

    @Test
    void testOfDisplayStringRejectsMissingSlash() {
        assertThrows(IllegalArgumentException.class, () -> ConwayTransitionRules.of("23"));
    }

    @Test
    void testOfDisplayStringRejectsMultipleSlashes() {
        assertThrows(IllegalArgumentException.class, () -> ConwayTransitionRules.of("2/3/4"));
    }

    // --- shouldSurvive ---

    @Test
    void testShouldSurviveReturnsTrueForMatchingCount() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("23/3");
        assertAll(
                () -> assertTrue(rules.shouldSurvive(2)),
                () -> assertTrue(rules.shouldSurvive(3))
        );
    }

    @Test
    void testShouldSurviveReturnsFalseForNonMatchingCount() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("23/3");
        assertAll(
                () -> assertFalse(rules.shouldSurvive(0)),
                () -> assertFalse(rules.shouldSurvive(1)),
                () -> assertFalse(rules.shouldSurvive(4))
        );
    }

    @Test
    void testShouldSurviveReturnsFalseForEmptyRules() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("/3");
        assertFalse(rules.shouldSurvive(2));
    }

    // --- shouldBeBorn ---

    @Test
    void testShouldBeBornReturnsTrueForMatchingCount() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("23/3");
        assertTrue(rules.shouldBeBorn(3));
    }

    @Test
    void testShouldBeBornReturnsFalseForNonMatchingCount() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("23/3");
        assertAll(
                () -> assertFalse(rules.shouldBeBorn(0)),
                () -> assertFalse(rules.shouldBeBorn(2)),
                () -> assertFalse(rules.shouldBeBorn(4))
        );
    }

    @Test
    void testShouldBeBornReturnsFalseForEmptyRules() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("23/");
        assertFalse(rules.shouldBeBorn(3));
    }

    // --- toDisplayString ---

    @Test
    void testToDisplayStringReturnsCorrectFormat() {
        ConwayTransitionRules rules = ConwayTransitionRules.of("23/3");
        assertEquals("23/3", rules.toDisplayString());
    }

    @Test
    void testToDisplayStringIsRoundTrippable() {
        String original = "1257/1357";
        ConwayTransitionRules rules = ConwayTransitionRules.of(original);
        assertEquals(original, rules.toDisplayString());
    }

    @Test
    void testOfDisplayStringRoundTripsFromRules() {
        ConwayTransitionRules original = ConwayTransitionRules.of(List.of(2, 3), List.of(3));
        ConwayTransitionRules reparsed = ConwayTransitionRules.of(original.toDisplayString());
        assertEquals(original, reparsed);
    }

    @Test
    void testOfDisplayStringSilentlyIgnoresNonDigitCharacters() {
        // Non-digit characters are silently skipped; same result as "23/3"
        ConwayTransitionRules withNoise = ConwayTransitionRules.of("2 3/3!");
        ConwayTransitionRules canonical = ConwayTransitionRules.of("23/3");
        assertEquals(canonical, withNoise);
    }

    @Test
    void testToDisplayStringWithEmptySets() {
        ConwayTransitionRules rules = new ConwayTransitionRules(new TreeSet<>(), new TreeSet<>());
        assertEquals("/", rules.toDisplayString());
    }

    @Test
    void testToDisplayStringDigitsAreSorted() {
        ConwayTransitionRules rules = ConwayTransitionRules.of(List.of(5, 2, 3), List.of(6, 3));
        assertEquals("235/36", rules.toDisplayString());
    }

    // --- equals and hashCode (record semantics) ---

    @Test
    void testEqualRulesAreEqual() {
        ConwayTransitionRules a = ConwayTransitionRules.of("23/3");
        ConwayTransitionRules b = ConwayTransitionRules.of("23/3");
        assertEquals(a, b);
    }

    @Test
    void testDifferentRulesAreNotEqual() {
        ConwayTransitionRules a = ConwayTransitionRules.of("23/3");
        ConwayTransitionRules b = ConwayTransitionRules.of("23/36");
        assertNotEquals(a, b);
    }

    @Test
    void testEqualRulesHaveSameHashCode() {
        ConwayTransitionRules a = ConwayTransitionRules.of("23/3");
        ConwayTransitionRules b = ConwayTransitionRules.of("23/3");
        assertEquals(a.hashCode(), b.hashCode());
    }

}

