package de.mkalb.etpetssim.simulations.lab.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class LabDistributionTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(LabDistribution.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(LabDistribution.values()).map(LabDistribution::resourceKey).toList());
    }

    @ParameterizedTest
    @EnumSource(LabDistribution.class)
    void testUsesHighlightedPercent(LabDistribution distribution) {
        boolean expected = switch (distribution) {
            case RANDOM_PERCENT, RANDOM_PROBABILITY -> true;
            case BORDER, PATTERN -> false;
        };

        assertEquals(expected, distribution.usesHighlightedPercent());
    }

    @ParameterizedTest
    @EnumSource(LabDistribution.class)
    void testUsesPattern(LabDistribution distribution) {
        assertEquals(distribution == LabDistribution.PATTERN, distribution.usesPattern());
    }

}
