package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GridEdgeBehaviorTest {

    @Test
    public void testEnumValues() {
        assertNotNull(GridEdgeBehavior.valueOf("BLOCK_X_BLOCK_Y"));
        assertNotNull(GridEdgeBehavior.valueOf("BLOCK_X_WRAP_Y"));
        assertNotNull(GridEdgeBehavior.valueOf("WRAP_X_BLOCK_Y"));
        assertNotNull(GridEdgeBehavior.valueOf("WRAP_X_WRAP_Y"));
        assertNotNull(GridEdgeBehavior.valueOf("ABSORB_XY"));
        assertNotNull(GridEdgeBehavior.valueOf("REFLECT_XY"));
    }

    @Test
    void testEnumCount() {
        assertEquals(6, GridEdgeBehavior.values().length, "There should be exactly 6 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, GridEdgeBehavior.BLOCK_X_BLOCK_Y.ordinal());
        assertEquals(1, GridEdgeBehavior.BLOCK_X_WRAP_Y.ordinal());
        assertEquals(2, GridEdgeBehavior.WRAP_X_BLOCK_Y.ordinal());
        assertEquals(3, GridEdgeBehavior.WRAP_X_WRAP_Y.ordinal());
        assertEquals(4, GridEdgeBehavior.ABSORB_XY.ordinal());
        assertEquals(5, GridEdgeBehavior.REFLECT_XY.ordinal());
    }

    @Test
    void testResourceKey() {
        assertEquals("gridedgebehavior.block_x_block_y", GridEdgeBehavior.BLOCK_X_BLOCK_Y.resourceKey(), "BLOCK_X_BLOCK_Y should have the correct resource key");
        assertEquals("gridedgebehavior.block_x_wrap_y", GridEdgeBehavior.BLOCK_X_WRAP_Y.resourceKey(), "BLOCK_X_WRAP_Y should have the correct resource key");
        assertEquals("gridedgebehavior.wrap_x_block_y", GridEdgeBehavior.WRAP_X_BLOCK_Y.resourceKey(), "WRAP_X_BLOCK_Y should have the correct resource key");
        assertEquals("gridedgebehavior.wrap_x_wrap_y", GridEdgeBehavior.WRAP_X_WRAP_Y.resourceKey(), "WRAP_X_WRAP_Y should have the correct resource key");
        assertEquals("gridedgebehavior.absorb_xy", GridEdgeBehavior.ABSORB_XY.resourceKey(), "ABSORB_XY should have the correct resource key");
        assertEquals("gridedgebehavior.reflect_xy", GridEdgeBehavior.REFLECT_XY.resourceKey(), "REFLECT_XY should have the correct resource key");
    }

    @Test
    void testLabelResourceKey() {
        assertEquals("gridedgebehavior.label", GridEdgeBehavior.labelResourceKey(), "GridEdgeBehavior should have the correct resource key");
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