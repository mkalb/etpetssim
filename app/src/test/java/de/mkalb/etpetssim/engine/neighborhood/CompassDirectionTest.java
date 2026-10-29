package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("MagicNumber")
final class CompassDirectionTest {

    private static final List<CompassDirection> CLOCKWISE_FROM_N = List.of(
            CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE,
            CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE,
            CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW,
            CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW
    );
    private static final List<CompassDirection> CLOCKWISE_FROM_S = List.of(
            CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW,
            CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW,
            CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE,
            CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE
    );
    private static final List<CompassDirection> COUNTERCLOCKWISE_FROM_N = List.of(
            CompassDirection.N, CompassDirection.NNW, CompassDirection.NW, CompassDirection.WNW,
            CompassDirection.W, CompassDirection.WSW, CompassDirection.SW, CompassDirection.SSW,
            CompassDirection.S, CompassDirection.SSE, CompassDirection.SE, CompassDirection.ESE,
            CompassDirection.E, CompassDirection.ENE, CompassDirection.NE, CompassDirection.NNE
    );
    private static final List<CompassDirection> COUNTERCLOCKWISE_FROM_S = List.of(
            CompassDirection.S, CompassDirection.SSE, CompassDirection.SE, CompassDirection.ESE,
            CompassDirection.E, CompassDirection.ENE, CompassDirection.NE, CompassDirection.NNE,
            CompassDirection.N, CompassDirection.NNW, CompassDirection.NW, CompassDirection.WNW,
            CompassDirection.W, CompassDirection.WSW, CompassDirection.SW, CompassDirection.SSW
    );
    private static final List<CompassDirection> SQUARE_RING =
            List.of(CompassDirection.N, CompassDirection.E, CompassDirection.S, CompassDirection.W);
    private static final List<CompassDirection> SQUARE_FULL_RING = List.of(
            CompassDirection.N, CompassDirection.NE, CompassDirection.E, CompassDirection.SE,
            CompassDirection.S, CompassDirection.SW, CompassDirection.W, CompassDirection.NW);
    private static final List<CompassDirection> HEXAGON_RING = List.of(
            CompassDirection.N, CompassDirection.NE, CompassDirection.SE,
            CompassDirection.S, CompassDirection.SW, CompassDirection.NW);

    private static void assertDistance(int expected, CompassDirection from, CompassDirection to) {
        assertAll(
                () -> assertEquals(expected, CompassDirection.distance(from, to)),
                () -> assertEquals(expected, CompassDirection.distance(to, from))
        );
    }

    private static void assertDistanceOnRing(int expected,
                                             CompassDirection from,
                                             CompassDirection to,
                                             List<CompassDirection> ring) {
        assertAll(
                () -> assertEquals(expected, CompassDirection.distanceOnRing(from, to, ring)),
                () -> assertEquals(expected, CompassDirection.distanceOnRing(to, from, ring))
        );
    }

    private static void tryAddDirection(List<CompassDirection> directions) {
        directions.add(CompassDirection.N);
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(CLOCKWISE_FROM_N.toArray(), CompassDirection.values());
    }

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(CompassDirection.labelResourceKey()));
    }

    @Test
    void testAbbreviationResourceKey() {
        assertKeysInAllBundles(Arrays.stream(CompassDirection.values())
                .map(CompassDirection::abbreviationResourceKey)
                .toList());
    }

    @Test
    void testNameResourceKey() {
        assertKeysInAllBundles(Arrays.stream(CompassDirection.values())
                .map(CompassDirection::nameResourceKey)
                .toList());
    }

    @Test
    void testDistance() {
        assertAll(
                () -> assertDistance(0, CompassDirection.N, CompassDirection.N),
                () -> assertDistance(0, CompassDirection.E, CompassDirection.E),
                () -> assertDistance(1, CompassDirection.N, CompassDirection.NNE),
                () -> assertDistance(1, CompassDirection.S, CompassDirection.SSE),
                () -> assertDistance(1, CompassDirection.NNW, CompassDirection.N),
                () -> assertDistance(2, CompassDirection.N, CompassDirection.NE),
                () -> assertDistance(2, CompassDirection.S, CompassDirection.SW),
                () -> assertDistance(3, CompassDirection.NNW, CompassDirection.NE),
                () -> assertDistance(7, CompassDirection.E, CompassDirection.WNW),
                () -> assertDistance(8, CompassDirection.N, CompassDirection.S),
                () -> assertDistance(8, CompassDirection.E, CompassDirection.W),
                () -> assertDistance(8, CompassDirection.NE, CompassDirection.SW)
        );
    }

    @Test
    void testDistanceOnRingReturnsDistance() {
        assertAll(
                () -> assertDistanceOnRing(0, CompassDirection.N, CompassDirection.N, SQUARE_RING),
                () -> assertDistanceOnRing(0, CompassDirection.E, CompassDirection.E, SQUARE_RING),
                () -> assertDistanceOnRing(1, CompassDirection.N, CompassDirection.E, SQUARE_RING),
                () -> assertDistanceOnRing(1, CompassDirection.E, CompassDirection.S, SQUARE_RING),
                () -> assertDistanceOnRing(1, CompassDirection.W, CompassDirection.N, SQUARE_RING),
                () -> assertDistanceOnRing(2, CompassDirection.N, CompassDirection.S, SQUARE_RING),
                () -> assertDistanceOnRing(2, CompassDirection.E, CompassDirection.W, SQUARE_RING),

                () -> assertDistanceOnRing(0, CompassDirection.N, CompassDirection.N, HEXAGON_RING),
                () -> assertDistanceOnRing(1, CompassDirection.N, CompassDirection.NE, HEXAGON_RING),
                () -> assertDistanceOnRing(2, CompassDirection.N, CompassDirection.SE, HEXAGON_RING),
                () -> assertDistanceOnRing(3, CompassDirection.N, CompassDirection.S, HEXAGON_RING),
                () -> assertDistanceOnRing(2, CompassDirection.N, CompassDirection.SW, HEXAGON_RING),
                () -> assertDistanceOnRing(1, CompassDirection.N, CompassDirection.NW, HEXAGON_RING),

                () -> assertDistanceOnRing(2, CompassDirection.N, CompassDirection.E, SQUARE_FULL_RING),
                () -> assertDistanceOnRing(4, CompassDirection.N, CompassDirection.S, SQUARE_FULL_RING)
        );
    }

    @Test
    void testDistanceOnRingRejectsMissingDirection() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> CompassDirection.distanceOnRing(CompassDirection.NNE, CompassDirection.N, SQUARE_RING)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.NNE, SQUARE_RING)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> CompassDirection.distanceOnRing(CompassDirection.E, CompassDirection.ENE, SQUARE_RING))
        );
    }

    @Test
    void testDistanceOnRingRejectsEmptyRing() {
        assertThrows(IllegalArgumentException.class,
                () -> CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.E, List.of()));
    }

    @Test
    void testLevel() {
        assertEquals(List.of(0, 2, 1, 2, 0, 2, 1, 2, 0, 2, 1, 2, 0, 2, 1, 2),
                CLOCKWISE_FROM_N.stream().map(CompassDirection::level).toList());
    }

    @Test
    void testArrow() {
        assertEquals(List.of("↑", "↑↗", "↗", "→↗", "→", "→↘", "↘", "↓↘", "↓", "↓↙", "↙", "←↙", "←", "←↖", "↖", "↑↖"),
                CLOCKWISE_FROM_N.stream().map(CompassDirection::arrow).toList());
    }

    @Test
    void testOpposite() {
        assertEquals(CLOCKWISE_FROM_S, CLOCKWISE_FROM_N.stream().map(CompassDirection::opposite).toList());
    }

    @Test
    void testNextClockwise() {
        assertAll(
                () -> assertEquals(CLOCKWISE_FROM_N.subList(1, 16),
                        CLOCKWISE_FROM_N.subList(0, 15).stream().map(CompassDirection::nextClockwise).toList()),
                () -> assertEquals(CompassDirection.N, CompassDirection.NNW.nextClockwise())
        );
    }

    @Test
    void testNextCounterClockwise() {
        assertAll(
                () -> assertEquals(COUNTERCLOCKWISE_FROM_N.subList(1, 16),
                        COUNTERCLOCKWISE_FROM_N.subList(0, 15).stream()
                                .map(CompassDirection::nextCounterClockwise)
                                .toList()),
                () -> assertEquals(CompassDirection.N, CompassDirection.NNE.nextCounterClockwise())
        );
    }

    @Test
    void testListClockwise() {
        assertAll(
                () -> assertEquals(CLOCKWISE_FROM_N, CompassDirection.N.listClockwise()),
                () -> assertEquals(CLOCKWISE_FROM_S, CompassDirection.S.listClockwise()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> tryAddDirection(CompassDirection.N.listClockwise()))
        );
    }

    @Test
    void testListCounterClockwise() {
        assertAll(
                () -> assertEquals(COUNTERCLOCKWISE_FROM_N, CompassDirection.N.listCounterClockwise()),
                () -> assertEquals(COUNTERCLOCKWISE_FROM_S, CompassDirection.S.listCounterClockwise()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> tryAddDirection(CompassDirection.N.listCounterClockwise()))
        );
    }

    @Test
    void testStreamClockwise() {
        assertAll(
                () -> assertEquals(CLOCKWISE_FROM_N, CompassDirection.N.streamClockwise().toList()),
                () -> assertEquals(CLOCKWISE_FROM_S, CompassDirection.S.streamClockwise().toList())
        );
    }

    @Test
    void testStreamCounterClockwise() {
        assertAll(
                () -> assertEquals(COUNTERCLOCKWISE_FROM_N, CompassDirection.N.streamCounterClockwise().toList()),
                () -> assertEquals(COUNTERCLOCKWISE_FROM_S, CompassDirection.S.streamCounterClockwise().toList())
        );
    }

}
