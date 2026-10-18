package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.*;

final class GridEdgeBehaviorTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(GridEdgeBehavior.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(GridEdgeBehavior.values()).map(GridEdgeBehavior::resourceKey).toList());
    }

    @Test
    void testEdgeBehaviorX() {
        assertEquals(EdgeBehavior.BLOCK, GridEdgeBehavior.BLOCK_XY.edgeBehaviorX());
        assertEquals(EdgeBehavior.WRAP, GridEdgeBehavior.WRAP_XY.edgeBehaviorX());
        assertEquals(EdgeBehavior.ABSORB, GridEdgeBehavior.ABSORB_XY.edgeBehaviorX());
        assertEquals(EdgeBehavior.BLOCK, GridEdgeBehavior.BLOCK_X_WRAP_Y.edgeBehaviorX());
        assertEquals(EdgeBehavior.WRAP, GridEdgeBehavior.WRAP_X_BLOCK_Y.edgeBehaviorX());
    }

    @Test
    void testEdgeBehaviorY() {
        assertEquals(EdgeBehavior.BLOCK, GridEdgeBehavior.BLOCK_XY.edgeBehaviorY());
        assertEquals(EdgeBehavior.WRAP, GridEdgeBehavior.WRAP_XY.edgeBehaviorY());
        assertEquals(EdgeBehavior.ABSORB, GridEdgeBehavior.ABSORB_XY.edgeBehaviorY());
        assertEquals(EdgeBehavior.WRAP, GridEdgeBehavior.BLOCK_X_WRAP_Y.edgeBehaviorY());
        assertEquals(EdgeBehavior.BLOCK, GridEdgeBehavior.WRAP_X_BLOCK_Y.edgeBehaviorY());
    }

    @Test
    void testHasEqualEdgeBehaviors() {
        assertTrue(GridEdgeBehavior.BLOCK_XY.hasEqualEdgeBehaviors());
        assertTrue(GridEdgeBehavior.WRAP_XY.hasEqualEdgeBehaviors());
        assertTrue(GridEdgeBehavior.ABSORB_XY.hasEqualEdgeBehaviors());
        assertFalse(GridEdgeBehavior.BLOCK_X_WRAP_Y.hasEqualEdgeBehaviors());
        assertFalse(GridEdgeBehavior.WRAP_X_BLOCK_Y.hasEqualEdgeBehaviors());
    }

}
