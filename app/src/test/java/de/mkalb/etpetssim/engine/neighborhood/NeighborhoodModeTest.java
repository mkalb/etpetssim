package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.*;

final class NeighborhoodModeTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(NeighborhoodMode.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(NeighborhoodMode.values()).map(NeighborhoodMode::resourceKey).toList());
    }

    @Test
    void testIncludesVertexNeighbors() {
        assertFalse(NeighborhoodMode.EDGES_ONLY.includesVertexNeighbors());
        assertTrue(NeighborhoodMode.EDGES_AND_VERTICES.includesVertexNeighbors());
    }

}
