package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class EdgeBehaviorResultTest {

    @Test
    void testRecord() {
        GridCoordinate original = new GridCoordinate(1, 2);
        GridCoordinate mapped = new GridCoordinate(3, 4);
        EdgeBehaviorResult result1 = new EdgeBehaviorResult(original, mapped, EdgeBehaviorAction.WRAPPED);
        EdgeBehaviorResult result2 = new EdgeBehaviorResult(original, mapped, EdgeBehaviorAction.WRAPPED);

        assertEquals(original, result1.original());
        assertEquals(mapped, result1.mapped());
        assertEquals(EdgeBehaviorAction.WRAPPED, result1.action());
        assertEquals(result1, result2);
    }

    @Test
    void testToDisplayString() {
        GridCoordinate mapped = new GridCoordinate(5, 6);
        EdgeBehaviorResult result = new EdgeBehaviorResult(mapped, mapped, EdgeBehaviorAction.VALID);
        String display = result.toDisplayString();
        assertTrue(display.contains("[VALID]"));
        assertTrue(display.contains(mapped.toDisplayString()));
    }

}
