package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("MagicNumber")
final class EdgeBehaviorResultTest {

    @Test
    void testToDisplayString() {
        GridCoordinate coordinate = new GridCoordinate(5, 6);
        EdgeBehaviorResult validResult = new EdgeBehaviorResult(coordinate, coordinate, EdgeBehaviorAction.VALID);
        EdgeBehaviorResult wrappedResult = new EdgeBehaviorResult(
                new GridCoordinate(-1, 0), new GridCoordinate(15, 0), EdgeBehaviorAction.WRAPPED);

        assertAll(
                () -> assertEquals("[valid] mapped: (5, 6)", validResult.toDisplayString()),
                () -> assertEquals("[wrapped] mapped: (15, 0)", wrappedResult.toDisplayString())
        );
    }

}
