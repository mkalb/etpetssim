package de.mkalb.etpetssim.simulations.conway.shared;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

final class ConwayRulePresetTest {

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testConstantsStartWithEmpty(PresetFamily family) {
        ConwayRulePreset first = family.presets().getFirst();
        assertAll(
                () -> assertEquals("", first.ruleString()),
                () -> assertEquals("", first.displayName()),
                () -> assertEquals(-1, first.recommendedDensityPercent())
        );
    }

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testRuleStringIsParsableAndUnique(PresetFamily family) {
        List<String> ruleStrings = family.selectablePresets().stream().map(ConwayRulePreset::ruleString).toList();
        Stream<Executable> parseChecks = ruleStrings.stream()
                                                    .map(ruleString -> () -> assertDoesNotThrow(
                                                            () -> ConwayTransitionRules.parse(ruleString), ruleString));
        assertEquals(ruleStrings.size(), Set.copyOf(ruleStrings).size());
        assertAll(parseChecks);
    }

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testRuleStringRespectsMaxNeighborCount(PresetFamily family) {
        Stream<Executable> checks = family.selectablePresets().stream()
                                          .map(preset -> () -> {
                                              ConwayTransitionRules rules = ConwayTransitionRules.parse(preset.ruleString());
                                              assertTrue(
                                                      rules.surviveCounts().stream().allMatch(n -> n <= family.maxNeighborCount())
                                                              && rules.birthCounts().stream().allMatch(n -> n <= family.maxNeighborCount()),
                                                      preset.displayName());
                                          });
        assertAll(checks);
    }

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testDisplayNameIsUnique(PresetFamily family) {
        List<String> displayNames = family.selectablePresets().stream().map(ConwayRulePreset::displayName).toList();
        assertAll(
                () -> assertEquals(displayNames.size(), Set.copyOf(displayNames).size()),
                () -> assertTrue(displayNames.stream().noneMatch(String::isBlank))
        );
    }

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testRecommendedDensityPercent(PresetFamily family) {
        Stream<Executable> checks = family.selectablePresets().stream()
                                          .map(preset -> () -> assertTrue(
                                                  (preset.recommendedDensityPercent() >= 1)
                                                          && (preset.recommendedDensityPercent() <= 100),
                                                  preset.displayName()));
        assertAll(checks);
    }

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testConstantsAreSortedByDisplayName(PresetFamily family) {
        List<ConwayRulePreset> presets = family.presets();
        int leadingCount = family.leadingPresets().size();
        List<ConwayRulePreset> sortedPart = presets.subList(leadingCount, presets.size());
        assertAll(
                () -> assertEquals(family.leadingPresets(), presets.subList(0, leadingCount)),
                () -> assertEquals(sortedPart.stream().sorted(Comparator.comparing(ConwayRulePreset::displayName)).toList(),
                        sortedPart)
        );
    }

    enum PresetFamily {
        HEXAGON(ConwayRulePresetHexagon.values(), List.of(ConwayRulePresetHexagon.EMPTY), 6),
        SQUARE(ConwayRulePresetSquare.values(),
                List.of(ConwayRulePresetSquare.EMPTY, ConwayRulePresetSquare.CONWAYS_LIFE), 8),
        TRIANGLE(ConwayRulePresetTriangle.values(), List.of(ConwayRulePresetTriangle.EMPTY),
                ConwayTransitionRules.MAX_NEIGHBOR_COUNT);

        private final List<ConwayRulePreset> presets;
        private final List<ConwayRulePreset> leadingPresets;
        private final int maxNeighborCount;

        PresetFamily(ConwayRulePreset[] presets, List<ConwayRulePreset> leadingPresets, int maxNeighborCount) {
            this.presets = List.of(presets);
            this.leadingPresets = leadingPresets;
            this.maxNeighborCount = maxNeighborCount;
        }

        List<ConwayRulePreset> presets() {
            return presets;
        }

        List<ConwayRulePreset> selectablePresets() {
            return presets.subList(1, presets.size());
        }

        List<ConwayRulePreset> leadingPresets() {
            return leadingPresets;
        }

        int maxNeighborCount() {
            return maxNeighborCount;
        }
    }

}
