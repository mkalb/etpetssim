package de.mkalb.etpetssim.simulations.core.viewmodel;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class SimulationExecutionModeTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(SimulationExecutionMode.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(SimulationExecutionMode.values())
                .map(SimulationExecutionMode::resourceKey).toList());
    }

}
