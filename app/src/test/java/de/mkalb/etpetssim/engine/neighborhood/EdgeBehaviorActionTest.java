package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class EdgeBehaviorActionTest {

    @Test
    void testEnumValues() {
        assertNotNull(EdgeBehaviorAction.valueOf("VALID"));
        assertNotNull(EdgeBehaviorAction.valueOf("BLOCKED"));
        assertNotNull(EdgeBehaviorAction.valueOf("WRAPPED"));
        assertNotNull(EdgeBehaviorAction.valueOf("ABSORBED"));
    }

    @Test
    void testEnumCount() {
        assertEquals(4, EdgeBehaviorAction.values().length, "There should be exactly 4 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new EdgeBehaviorAction[]{
                        EdgeBehaviorAction.VALID,
                        EdgeBehaviorAction.BLOCKED,
                        EdgeBehaviorAction.WRAPPED,
                        EdgeBehaviorAction.ABSORBED
                },
                EdgeBehaviorAction.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> EdgeBehaviorAction.valueOf("INVALID"));
    }

}
