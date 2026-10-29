package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.CellShape;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LangtonRulePresetTest {

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testConstantsStartWithEmpty(PresetFamily family) {
        LangtonRulePreset first = family.presets().getFirst();
        assertAll(
                () -> assertEquals("", first.ruleString()),
                () -> assertEquals("", first.displayName())
        );
    }

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testRuleStringIsParsableAndUnique(PresetFamily family) {
        List<String> ruleStrings = family.selectablePresets().stream().map(LangtonRulePreset::ruleString).toList();
        Stream<Executable> parseChecks = ruleStrings.stream()
                .map(ruleString -> () -> assertDoesNotThrow(
                        () -> LangtonMovementRules.parse(ruleString), ruleString));
        assertEquals(ruleStrings.size(), Set.copyOf(ruleStrings).size());
        assertAll(parseChecks);
    }

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testRuleStringIsValidForCellShape(PresetFamily family) {
        Stream<Executable> checks = family.selectablePresets().stream()
                .map(preset -> () -> assertTrue(
                        LangtonMovementRules.parse(preset.ruleString())
                                .isValidForCellShape(family.cellShape()),
                        preset.ruleString()));
        assertAll(checks);
    }

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testDisplayName(PresetFamily family) {
        Stream<Executable> checks = family.presets().stream()
                .map(preset -> () -> assertEquals(preset.ruleString(), preset.displayName()));
        assertAll(checks);
    }

    @ParameterizedTest
    @EnumSource(PresetFamily.class)
    void testConstantsAreSortedByLengthThenAlphabetically(PresetFamily family) {
        List<String> ruleStrings = family.selectablePresets().stream().map(LangtonRulePreset::ruleString).toList();
        assertEquals(ruleStrings.stream()
                        .sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
                        .toList(),
                ruleStrings);
    }

    enum PresetFamily {
        HEXAGON(LangtonRulePresetHexagon.values(), CellShape.HEXAGON),
        SQUARE(LangtonRulePresetSquare.values(), CellShape.SQUARE),
        TRIANGLE(LangtonRulePresetTriangle.values(), CellShape.TRIANGLE);

        private final List<LangtonRulePreset> presets;
        private final CellShape cellShape;

        PresetFamily(LangtonRulePreset[] presets, CellShape cellShape) {
            this.presets = List.of(presets);
            this.cellShape = cellShape;
        }

        List<LangtonRulePreset> presets() {
            return presets;
        }

        List<LangtonRulePreset> selectablePresets() {
            return presets.subList(1, presets.size());
        }

        CellShape cellShape() {
            return cellShape;
        }
    }

}
