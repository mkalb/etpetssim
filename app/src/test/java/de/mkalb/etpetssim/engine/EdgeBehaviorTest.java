package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class EdgeBehaviorTest {

    @Test
    public void testEnumValues() {
        assertNotNull(EdgeBehavior.valueOf("BLOCK"));
        assertNotNull(EdgeBehavior.valueOf("WRAP"));
        assertNotNull(EdgeBehavior.valueOf("ABSORB"));
        assertNotNull(EdgeBehavior.valueOf("REFLECT"));
    }

    @Test
    void testEnumCount() {
        assertEquals(4, EdgeBehavior.values().length, "There should be exactly 4 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, EdgeBehavior.BLOCK.ordinal());
        assertEquals(1, EdgeBehavior.WRAP.ordinal());
        assertEquals(2, EdgeBehavior.ABSORB.ordinal());
        assertEquals(3, EdgeBehavior.REFLECT.ordinal());
    }

    @Test
    void testStaticLabelResourceKey() {
        assertEquals("edgebehavior.label", EdgeBehavior.labelResourceKey(), "EdgeBehavior should have the correct resource key");
    }

    @Test
    void testResourceKey() {
        assertEquals("edgebehavior.block", EdgeBehavior.BLOCK.resourceKey(), "BLOCK should have the correct resource key");
        assertEquals("edgebehavior.wrap", EdgeBehavior.WRAP.resourceKey(), "WRAP should have the correct resource key");
        assertEquals("edgebehavior.absorb", EdgeBehavior.ABSORB.resourceKey(), "ABSORB should have the correct resource key");
        assertEquals("edgebehavior.reflect", EdgeBehavior.REFLECT.resourceKey(), "REFLECT should have the correct resource key");
    }

}
