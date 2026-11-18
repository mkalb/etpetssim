package de.mkalb.etpetssim.simulations.lab.shared;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class LabBaseLayerTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(LabBaseLayer.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(LabBaseLayer.values()).map(LabBaseLayer::resourceKey).toList());
    }

}
