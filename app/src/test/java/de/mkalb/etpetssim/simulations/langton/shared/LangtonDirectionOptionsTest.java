package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class LangtonDirectionOptionsTest {

    private static final GridCoordinate TRIANGLE_POINTING_DOWN = new GridCoordinate(0, 0);
    private static final GridCoordinate TRIANGLE_POINTING_UP = new GridCoordinate(1, 0);

    @Test
    void testSelectableInitialDirections() {
        assertAll(
                () -> assertEquals(List.of(CompassDirection.N, CompassDirection.E, CompassDirection.S,
                                CompassDirection.W),
                        LangtonDirectionOptions.selectableInitialDirections(CellShape.SQUARE)),
                () -> assertEquals(List.of(CompassDirection.N, CompassDirection.NE, CompassDirection.SE,
                                CompassDirection.S, CompassDirection.SW, CompassDirection.NW),
                        LangtonDirectionOptions.selectableInitialDirections(CellShape.HEXAGON)),
                () -> assertTrue(LangtonDirectionOptions.selectableInitialDirections(CellShape.TRIANGLE).isEmpty())
        );
    }

    @Test
    void testResolveInitialDirection() {
        assertAll(
                () -> assertEquals(Optional.of(CompassDirection.W),
                        LangtonDirectionOptions.resolveInitialDirection(CellShape.SQUARE, null, CompassDirection.W)),
                () -> assertEquals(Optional.of(CompassDirection.NE),
                        LangtonDirectionOptions.resolveInitialDirection(CellShape.HEXAGON, null, CompassDirection.NE))
        );
    }

    @Test
    void testResolveInitialDirectionReturnsEmptyForMissingOrInvalidDirection() {
        assertAll(
                () -> assertTrue(LangtonDirectionOptions.resolveInitialDirection(
                        CellShape.SQUARE, TRIANGLE_POINTING_DOWN, null).isEmpty()),
                () -> assertTrue(LangtonDirectionOptions.resolveInitialDirection(
                        CellShape.SQUARE, null, CompassDirection.NE).isEmpty()),
                () -> assertTrue(LangtonDirectionOptions.resolveInitialDirection(
                        CellShape.HEXAGON, null, CompassDirection.E).isEmpty())
        );
    }

    @Test
    void testResolveInitialDirectionForTriangleIgnoresSelectedDirection() {
        assertAll(
                () -> assertEquals(Optional.of(CompassDirection.N), LangtonDirectionOptions.resolveInitialDirection(
                        CellShape.TRIANGLE, TRIANGLE_POINTING_DOWN, CompassDirection.S)),
                () -> assertEquals(Optional.of(CompassDirection.S), LangtonDirectionOptions.resolveInitialDirection(
                        CellShape.TRIANGLE, TRIANGLE_POINTING_UP, null)),
                () -> assertTrue(LangtonDirectionOptions.resolveInitialDirection(
                        CellShape.TRIANGLE, null, CompassDirection.N).isEmpty())
        );
    }

    @Test
    void testValidInitialDirections() {
        assertAll(
                () -> assertEquals(LangtonDirectionOptions.selectableInitialDirections(CellShape.SQUARE),
                        LangtonDirectionOptions.validInitialDirections(CellShape.SQUARE, TRIANGLE_POINTING_DOWN)),
                () -> assertEquals(LangtonDirectionOptions.selectableInitialDirections(CellShape.HEXAGON),
                        LangtonDirectionOptions.validInitialDirections(CellShape.HEXAGON, TRIANGLE_POINTING_DOWN)),
                () -> assertEquals(List.of(CompassDirection.N),
                        LangtonDirectionOptions.validInitialDirections(CellShape.TRIANGLE, TRIANGLE_POINTING_DOWN)),
                () -> assertEquals(List.of(CompassDirection.S),
                        LangtonDirectionOptions.validInitialDirections(CellShape.TRIANGLE, TRIANGLE_POINTING_UP))
        );
    }

}
