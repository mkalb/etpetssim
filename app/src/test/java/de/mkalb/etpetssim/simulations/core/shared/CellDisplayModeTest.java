package de.mkalb.etpetssim.simulations.core.shared;

import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.*;

final class CellDisplayModeTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(CellDisplayMode.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(CellDisplayMode.values()).map(CellDisplayMode::resourceKey).toList());
    }

    @Test
    void testHasBorder() {
        assertFalse(CellDisplayMode.SHAPE.hasBorder());
        assertTrue(CellDisplayMode.SHAPE_BORDERED.hasBorder());
        assertFalse(CellDisplayMode.CIRCLE.hasBorder());
        assertTrue(CellDisplayMode.CIRCLE_BORDERED.hasBorder());
        assertFalse(CellDisplayMode.EMOJI.hasBorder());
    }

}
