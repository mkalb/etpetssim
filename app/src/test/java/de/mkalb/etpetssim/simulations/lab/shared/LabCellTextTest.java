package de.mkalb.etpetssim.simulations.lab.shared;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class LabCellTextTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(LabCellText.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(LabCellText.values()).map(LabCellText::resourceKey).toList());
    }

}
