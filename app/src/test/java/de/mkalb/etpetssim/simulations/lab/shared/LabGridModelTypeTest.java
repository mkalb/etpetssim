package de.mkalb.etpetssim.simulations.lab.shared;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class LabGridModelTypeTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(LabGridModelType.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(LabGridModelType.values()).map(LabGridModelType::resourceKey).toList());
    }

}
