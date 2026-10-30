package de.mkalb.etpetssim.simulations.lab.shared;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class LabColorModeTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(LabColorMode.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(LabColorMode.values()).map(LabColorMode::resourceKey).toList());
    }

}
