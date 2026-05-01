package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class EdgeBehaviorTest {

    @Test
    void testEnumValues() {
        assertNotNull(EdgeBehavior.valueOf("BLOCK"));
        assertNotNull(EdgeBehavior.valueOf("WRAP"));
        assertNotNull(EdgeBehavior.valueOf("ABSORB"));
    }

    @Test
    void testEnumCount() {
        assertEquals(3, EdgeBehavior.values().length, "There should be exactly 3 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new EdgeBehavior[]{EdgeBehavior.BLOCK, EdgeBehavior.WRAP, EdgeBehavior.ABSORB},
                EdgeBehavior.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> EdgeBehavior.valueOf("INVALID"));
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
    }

}
