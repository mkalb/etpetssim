package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GridEdgeBehaviorTest {

    @Test
    public void testEnumValues() {
        assertEquals(6, GridEdgeBehavior.values().length);
        assertNotNull(GridEdgeBehavior.valueOf("BLOCK_X_BLOCK_Y"));
        assertNotNull(GridEdgeBehavior.valueOf("BLOCK_X_WRAP_Y"));
        assertNotNull(GridEdgeBehavior.valueOf("WRAP_X_BLOCK_Y"));
        assertNotNull(GridEdgeBehavior.valueOf("WRAP_X_WRAP_Y"));
        assertNotNull(GridEdgeBehavior.valueOf("ABSORB_XY"));
        assertNotNull(GridEdgeBehavior.valueOf("REFLECT_XY"));
    }

    @Test
    public void testEdgeBehaviorAccessors() {
        assertEquals(EdgeBehavior.BLOCK, GridEdgeBehavior.BLOCK_X_BLOCK_Y.edgeBehaviorX());
        assertEquals(EdgeBehavior.BLOCK, GridEdgeBehavior.BLOCK_X_BLOCK_Y.edgeBehaviorY());

        assertEquals(EdgeBehavior.BLOCK, GridEdgeBehavior.BLOCK_X_WRAP_Y.edgeBehaviorX());
        assertEquals(EdgeBehavior.WRAP, GridEdgeBehavior.BLOCK_X_WRAP_Y.edgeBehaviorY());

        assertEquals(EdgeBehavior.WRAP, GridEdgeBehavior.WRAP_X_BLOCK_Y.edgeBehaviorX());
        assertEquals(EdgeBehavior.BLOCK, GridEdgeBehavior.WRAP_X_BLOCK_Y.edgeBehaviorY());

        assertEquals(EdgeBehavior.WRAP, GridEdgeBehavior.WRAP_X_WRAP_Y.edgeBehaviorX());
        assertEquals(EdgeBehavior.WRAP, GridEdgeBehavior.WRAP_X_WRAP_Y.edgeBehaviorY());

        assertEquals(EdgeBehavior.ABSORB, GridEdgeBehavior.ABSORB_XY.edgeBehaviorX());
        assertEquals(EdgeBehavior.ABSORB, GridEdgeBehavior.ABSORB_XY.edgeBehaviorY());

        assertEquals(EdgeBehavior.REFLECT, GridEdgeBehavior.REFLECT_XY.edgeBehaviorX());
        assertEquals(EdgeBehavior.REFLECT, GridEdgeBehavior.REFLECT_XY.edgeBehaviorY());
    }

    @Test
    public void testIsEqualEdgeBehavior() {
        assertTrue(GridEdgeBehavior.BLOCK_X_BLOCK_Y.isEqualEdgeBehavior());
        assertFalse(GridEdgeBehavior.BLOCK_X_WRAP_Y.isEqualEdgeBehavior());
        assertFalse(GridEdgeBehavior.WRAP_X_BLOCK_Y.isEqualEdgeBehavior());
        assertTrue(GridEdgeBehavior.WRAP_X_WRAP_Y.isEqualEdgeBehavior());
        assertTrue(GridEdgeBehavior.ABSORB_XY.isEqualEdgeBehavior());
        assertTrue(GridEdgeBehavior.REFLECT_XY.isEqualEdgeBehavior());
    }

}