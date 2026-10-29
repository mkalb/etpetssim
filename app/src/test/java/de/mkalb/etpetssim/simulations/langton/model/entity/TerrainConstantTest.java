package de.mkalb.etpetssim.simulations.langton.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertDescriptorKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("MagicNumber")
final class TerrainConstantTest {

    @Test
    void testDescriptorSpec() {
        List<TerrainConstant> constants = List.of(TerrainConstant.values());
        List<String> descriptorIds = constants.stream().map(TerrainConstant::descriptorId).toList();
        assertAll(
                () -> assertEquals(descriptorIds.size(), Set.copyOf(descriptorIds).size()),
                () -> assertDescriptorKeysInAllBundles(constants)
        );
    }

    @Test
    void testRequireByRuleIndex() {
        Stream<Executable> checks = Arrays.stream(TerrainConstant.values())
                .map(constant -> () -> assertSame(constant,
                        TerrainConstant.requireByRuleIndex(constant.ruleIndex())));
        assertAll(checks);
    }

    @Test
    void testRequireByRuleIndexRejectsUnknownIndex() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> TerrainConstant.requireByRuleIndex(-2)),
                () -> assertThrows(IllegalArgumentException.class, () -> TerrainConstant.requireByRuleIndex(16))
        );
    }

}
