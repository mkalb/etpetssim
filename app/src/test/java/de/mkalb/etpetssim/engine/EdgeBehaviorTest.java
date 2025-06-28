package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeBehaviorTest {

    @Test
    public void testEnumValues() {
        assertEquals(4, EdgeBehavior.values().length);
        assertNotNull(EdgeBehavior.valueOf("BLOCK"));
        assertNotNull(EdgeBehavior.valueOf("WRAP"));
        assertNotNull(EdgeBehavior.valueOf("ABSORB"));
        assertNotNull(EdgeBehavior.valueOf("REFLECT"));
    }

}