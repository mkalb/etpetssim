package de.mkalb.etpetssim.simulations.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

final class SimulationCategoryTest {

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new SimulationCategory[]{
                        SimulationCategory.AGENT_BASED_MODEL,
                        SimulationCategory.CELLULAR_AUTOMATON,
                        SimulationCategory.TOY_MODEL
                },
                SimulationCategory.values()
        );
    }

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(SimulationCategory.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(
                Arrays.stream(SimulationCategory.values()).map(SimulationCategory::resourceKey).toList());
    }

}
