package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoundaryTypeTest {

    @Test
    public void testEnumValues() {
        assertEquals(6, BoundaryType.values().length);
        assertNotNull(BoundaryType.valueOf("BLOCK_X_BLOCK_Y"));
        assertNotNull(BoundaryType.valueOf("BLOCK_X_WRAP_Y"));
        assertNotNull(BoundaryType.valueOf("WRAP_X_BLOCK_Y"));
        assertNotNull(BoundaryType.valueOf("WRAP_X_WRAP_Y"));
        assertNotNull(BoundaryType.valueOf("ABSORB_XY"));
        assertNotNull(BoundaryType.valueOf("REFLECT_XY"));
    }

    @Test
    public void testEdgeBehaviorAccessors() {
        assertEquals(EdgeBehavior.BLOCK, BoundaryType.BLOCK_X_BLOCK_Y.edgeBehaviorX());
        assertEquals(EdgeBehavior.BLOCK, BoundaryType.BLOCK_X_BLOCK_Y.edgeBehaviorY());

        assertEquals(EdgeBehavior.BLOCK, BoundaryType.BLOCK_X_WRAP_Y.edgeBehaviorX());
        assertEquals(EdgeBehavior.WRAP, BoundaryType.BLOCK_X_WRAP_Y.edgeBehaviorY());

        assertEquals(EdgeBehavior.WRAP, BoundaryType.WRAP_X_BLOCK_Y.edgeBehaviorX());
        assertEquals(EdgeBehavior.BLOCK, BoundaryType.WRAP_X_BLOCK_Y.edgeBehaviorY());

        assertEquals(EdgeBehavior.WRAP, BoundaryType.WRAP_X_WRAP_Y.edgeBehaviorX());
        assertEquals(EdgeBehavior.WRAP, BoundaryType.WRAP_X_WRAP_Y.edgeBehaviorY());

        assertEquals(EdgeBehavior.ABSORB, BoundaryType.ABSORB_XY.edgeBehaviorX());
        assertEquals(EdgeBehavior.ABSORB, BoundaryType.ABSORB_XY.edgeBehaviorY());

        assertEquals(EdgeBehavior.REFLECT, BoundaryType.REFLECT_XY.edgeBehaviorX());
        assertEquals(EdgeBehavior.REFLECT, BoundaryType.REFLECT_XY.edgeBehaviorY());
    }

    @Test
    public void testIsEqualEdgeBehavior() {
        assertTrue(BoundaryType.BLOCK_X_BLOCK_Y.isEqualEdgeBehavior());
        assertFalse(BoundaryType.BLOCK_X_WRAP_Y.isEqualEdgeBehavior());
        assertFalse(BoundaryType.WRAP_X_BLOCK_Y.isEqualEdgeBehavior());
        assertTrue(BoundaryType.WRAP_X_WRAP_Y.isEqualEdgeBehavior());
        assertTrue(BoundaryType.ABSORB_XY.isEqualEdgeBehavior());
        assertTrue(BoundaryType.REFLECT_XY.isEqualEdgeBehavior());
    }

}