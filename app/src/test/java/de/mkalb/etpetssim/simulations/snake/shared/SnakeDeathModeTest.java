package de.mkalb.etpetssim.simulations.snake.shared;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class SnakeDeathModeTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(SnakeDeathMode.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(SnakeDeathMode.values()).map(SnakeDeathMode::resourceKey).toList());
    }

}
