package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class CellConnectionTypeTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(CellConnectionType.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(CellConnectionType.values()).map(CellConnectionType::resourceKey).toList());
    }

}
