package de.mkalb.etpetssim.simulations.lab.shared;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class LabPatternTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(LabPattern.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(LabPattern.values()).map(LabPattern::resourceKey).toList());
    }

}
