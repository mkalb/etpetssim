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
        assertNotNull(EdgeBehaviorAction.valueOf("REFLECTED"));
    }

    @Test
    void testEnumCount() {
        assertEquals(5, EdgeBehaviorAction.values().length, "There should be exactly 5 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, EdgeBehaviorAction.VALID.ordinal());
        assertEquals(1, EdgeBehaviorAction.BLOCKED.ordinal());
        assertEquals(2, EdgeBehaviorAction.WRAPPED.ordinal());
        assertEquals(3, EdgeBehaviorAction.ABSORBED.ordinal());
        assertEquals(4, EdgeBehaviorAction.REFLECTED.ordinal());
    }

}
