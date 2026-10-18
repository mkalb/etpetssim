package de.mkalb.etpetssim.simulations.core.shared;

import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class SimulationModeTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(SimulationMode.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(SimulationMode.values()).map(SimulationMode::resourceKey).toList());
    }

}
