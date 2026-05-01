package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GridEdgeBehaviorTest {

    @Test
    void testEnumValues() {
        assertNotNull(GridEdgeBehavior.valueOf("BLOCK_XY"));
        assertNotNull(GridEdgeBehavior.valueOf("WRAP_XY"));
        assertNotNull(GridEdgeBehavior.valueOf("ABSORB_XY"));
        assertNotNull(GridEdgeBehavior.valueOf("BLOCK_X_WRAP_Y"));
        assertNotNull(GridEdgeBehavior.valueOf("WRAP_X_BLOCK_Y"));
    }

    @Test
    void testEnumCount() {
        assertEquals(5, GridEdgeBehavior.values().length, "There should be exactly 5 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new GridEdgeBehavior[]{
                        GridEdgeBehavior.BLOCK_XY,
                        GridEdgeBehavior.WRAP_XY,
                        GridEdgeBehavior.ABSORB_XY,
                        GridEdgeBehavior.BLOCK_X_WRAP_Y,
                        GridEdgeBehavior.WRAP_X_BLOCK_Y
                },
                GridEdgeBehavior.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> GridEdgeBehavior.valueOf("INVALID"));
    }

    @Test
    void testStaticLabelResourceKey() {
        assertEquals("gridedgebehavior.label", GridEdgeBehavior.labelResourceKey(), "GridEdgeBehavior should have the correct resource key");
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

    @Test
    void testResourceKey() {
        assertEquals("gridedgebehavior.block_xy", GridEdgeBehavior.BLOCK_XY.resourceKey(), "BLOCK_XY should have the correct resource key");
        assertEquals("gridedgebehavior.wrap_xy", GridEdgeBehavior.WRAP_XY.resourceKey(), "WRAP_XY should have the correct resource key");
        assertEquals("gridedgebehavior.absorb_xy", GridEdgeBehavior.ABSORB_XY.resourceKey(), "ABSORB_XY should have the correct resource key");
        assertEquals("gridedgebehavior.block_x_wrap_y", GridEdgeBehavior.BLOCK_X_WRAP_Y.resourceKey(), "BLOCK_X_WRAP_Y should have the correct resource key");
        assertEquals("gridedgebehavior.wrap_x_block_y", GridEdgeBehavior.WRAP_X_BLOCK_Y.resourceKey(), "WRAP_X_BLOCK_Y should have the correct resource key");
    }

}
