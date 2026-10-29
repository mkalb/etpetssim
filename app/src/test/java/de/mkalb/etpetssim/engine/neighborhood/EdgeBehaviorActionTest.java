package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EdgeBehaviorActionTest {

    @Test
    void testIsWithinGrid() {
        assertAll(
                () -> assertTrue(EdgeBehaviorAction.VALID.isWithinGrid()),
                () -> assertFalse(EdgeBehaviorAction.BLOCKED.isWithinGrid()),
                () -> assertTrue(EdgeBehaviorAction.WRAPPED.isWithinGrid()),
                () -> assertFalse(EdgeBehaviorAction.ABSORBED.isWithinGrid())
        );
    }

}
