package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class EdgeBehaviorTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(EdgeBehavior.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(EdgeBehavior.values()).map(EdgeBehavior::resourceKey).toList());
    }

}
