package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class CompassDirectionTest {

    @Test
    void testEnumValues() {
        assertNotNull(CompassDirection.valueOf("N"));
        assertNotNull(CompassDirection.valueOf("NNE"));
        assertNotNull(CompassDirection.valueOf("NE"));
        assertNotNull(CompassDirection.valueOf("ENE"));
        assertNotNull(CompassDirection.valueOf("E"));
        assertNotNull(CompassDirection.valueOf("ESE"));
        assertNotNull(CompassDirection.valueOf("SE"));
        assertNotNull(CompassDirection.valueOf("SSE"));
        assertNotNull(CompassDirection.valueOf("S"));
        assertNotNull(CompassDirection.valueOf("SSW"));
        assertNotNull(CompassDirection.valueOf("SW"));
        assertNotNull(CompassDirection.valueOf("WSW"));
        assertNotNull(CompassDirection.valueOf("W"));
        assertNotNull(CompassDirection.valueOf("WNW"));
        assertNotNull(CompassDirection.valueOf("NW"));
        assertNotNull(CompassDirection.valueOf("NNW"));
    }

    @Test
    void testEnumCount() {
        assertEquals(16, CompassDirection.values().length, "There should be exactly 16 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, CompassDirection.N.ordinal());
        assertEquals(1, CompassDirection.NNE.ordinal());
        assertEquals(2, CompassDirection.NE.ordinal());
        assertEquals(3, CompassDirection.ENE.ordinal());
        assertEquals(4, CompassDirection.E.ordinal());
        assertEquals(5, CompassDirection.ESE.ordinal());
        assertEquals(6, CompassDirection.SE.ordinal());
        assertEquals(7, CompassDirection.SSE.ordinal());
        assertEquals(8, CompassDirection.S.ordinal());
        assertEquals(9, CompassDirection.SSW.ordinal());
        assertEquals(10, CompassDirection.SW.ordinal());
        assertEquals(11, CompassDirection.WSW.ordinal());
        assertEquals(12, CompassDirection.W.ordinal());
        assertEquals(13, CompassDirection.WNW.ordinal());
        assertEquals(14, CompassDirection.NW.ordinal());
        assertEquals(15, CompassDirection.NNW.ordinal());
    }

    @Test
    void testStaticLabelResourceKey() {
        assertEquals("compass.label", CompassDirection.labelResourceKey());
    }

    @Test
    void testAbbrResourceKeys() {
        assertEquals("compass.abbr.n", CompassDirection.N.abbrResourceKey());
        assertEquals("compass.abbr.nne", CompassDirection.NNE.abbrResourceKey());
        assertEquals("compass.abbr.ne", CompassDirection.NE.abbrResourceKey());
        assertEquals("compass.abbr.ene", CompassDirection.ENE.abbrResourceKey());
        assertEquals("compass.abbr.e", CompassDirection.E.abbrResourceKey());
        assertEquals("compass.abbr.ese", CompassDirection.ESE.abbrResourceKey());
        assertEquals("compass.abbr.se", CompassDirection.SE.abbrResourceKey());
        assertEquals("compass.abbr.sse", CompassDirection.SSE.abbrResourceKey());
        assertEquals("compass.abbr.s", CompassDirection.S.abbrResourceKey());
        assertEquals("compass.abbr.ssw", CompassDirection.SSW.abbrResourceKey());
        assertEquals("compass.abbr.sw", CompassDirection.SW.abbrResourceKey());
        assertEquals("compass.abbr.wsw", CompassDirection.WSW.abbrResourceKey());
        assertEquals("compass.abbr.w", CompassDirection.W.abbrResourceKey());
        assertEquals("compass.abbr.wnw", CompassDirection.WNW.abbrResourceKey());
        assertEquals("compass.abbr.nw", CompassDirection.NW.abbrResourceKey());
        assertEquals("compass.abbr.nnw", CompassDirection.NNW.abbrResourceKey());
    }

    @Test
    void testNameResourceKey() {
        assertEquals("compass.name.n", CompassDirection.N.nameResourceKey());
        assertEquals("compass.name.nne", CompassDirection.NNE.nameResourceKey());
        assertEquals("compass.name.ne", CompassDirection.NE.nameResourceKey());
        assertEquals("compass.name.ene", CompassDirection.ENE.nameResourceKey());
        assertEquals("compass.name.e", CompassDirection.E.nameResourceKey());
        assertEquals("compass.name.ese", CompassDirection.ESE.nameResourceKey());
        assertEquals("compass.name.se", CompassDirection.SE.nameResourceKey());
        assertEquals("compass.name.sse", CompassDirection.SSE.nameResourceKey());
        assertEquals("compass.name.s", CompassDirection.S.nameResourceKey());
        assertEquals("compass.name.ssw", CompassDirection.SSW.nameResourceKey());
        assertEquals("compass.name.sw", CompassDirection.SW.nameResourceKey());
        assertEquals("compass.name.wsw", CompassDirection.WSW.nameResourceKey());
        assertEquals("compass.name.w", CompassDirection.W.nameResourceKey());
        assertEquals("compass.name.wnw", CompassDirection.WNW.nameResourceKey());
        assertEquals("compass.name.nw", CompassDirection.NW.nameResourceKey());
        assertEquals("compass.name.nnw", CompassDirection.NNW.nameResourceKey());
    }

    @Test
    void testLevel() {
        assertEquals(0, CompassDirection.N.level());
        assertEquals(2, CompassDirection.NNE.level());
        assertEquals(1, CompassDirection.NE.level());
        assertEquals(2, CompassDirection.ENE.level());
        assertEquals(0, CompassDirection.E.level());
        assertEquals(2, CompassDirection.ESE.level());
        assertEquals(1, CompassDirection.SE.level());
        assertEquals(2, CompassDirection.SSE.level());
        assertEquals(0, CompassDirection.S.level());
        assertEquals(2, CompassDirection.SSW.level());
        assertEquals(1, CompassDirection.SW.level());
        assertEquals(2, CompassDirection.WSW.level());
        assertEquals(0, CompassDirection.W.level());
        assertEquals(2, CompassDirection.WNW.level());
        assertEquals(1, CompassDirection.NW.level());
        assertEquals(2, CompassDirection.NNW.level());
    }

    @Test
    void testArrow() {
        assertEquals("↑", CompassDirection.N.arrow());
        assertEquals("↑↗", CompassDirection.NNE.arrow());
        assertEquals("↗", CompassDirection.NE.arrow());
        assertEquals("→↗", CompassDirection.ENE.arrow());
        assertEquals("→", CompassDirection.E.arrow());
        assertEquals("→↘", CompassDirection.ESE.arrow());
        assertEquals("↘", CompassDirection.SE.arrow());
        assertEquals("↓↘", CompassDirection.SSE.arrow());
        assertEquals("↓", CompassDirection.S.arrow());
        assertEquals("↓↙", CompassDirection.SSW.arrow());
        assertEquals("↙", CompassDirection.SW.arrow());
        assertEquals("←↙", CompassDirection.WSW.arrow());
        assertEquals("←", CompassDirection.W.arrow());
        assertEquals("←↖", CompassDirection.WNW.arrow());
        assertEquals("↖", CompassDirection.NW.arrow());
        assertEquals("↑↖", CompassDirection.NNW.arrow());
    }

    @Test
    void testOpposite() {
        assertEquals(CompassDirection.S, CompassDirection.N.opposite());
        assertEquals(CompassDirection.N, CompassDirection.S.opposite());
        assertEquals(CompassDirection.SSW, CompassDirection.NNE.opposite());
        assertEquals(CompassDirection.NNE, CompassDirection.SSW.opposite());
        assertEquals(CompassDirection.SW, CompassDirection.NE.opposite());
        assertEquals(CompassDirection.NE, CompassDirection.SW.opposite());
        assertEquals(CompassDirection.WSW, CompassDirection.ENE.opposite());
        assertEquals(CompassDirection.ENE, CompassDirection.WSW.opposite());
        assertEquals(CompassDirection.W, CompassDirection.E.opposite());
        assertEquals(CompassDirection.E, CompassDirection.W.opposite());
        assertEquals(CompassDirection.WNW, CompassDirection.ESE.opposite());
        assertEquals(CompassDirection.ESE, CompassDirection.WNW.opposite());
        assertEquals(CompassDirection.NW, CompassDirection.SE.opposite());
        assertEquals(CompassDirection.SE, CompassDirection.NW.opposite());
        assertEquals(CompassDirection.NNW, CompassDirection.SSE.opposite());
        assertEquals(CompassDirection.SSE, CompassDirection.NNW.opposite());
    }

    @Test
    void testNextClockwise() {
        assertEquals(CompassDirection.NNE, CompassDirection.N.nextClockwise());
        assertEquals(CompassDirection.NE, CompassDirection.NNE.nextClockwise());
        assertEquals(CompassDirection.ENE, CompassDirection.NE.nextClockwise());
        assertEquals(CompassDirection.E, CompassDirection.ENE.nextClockwise());
        assertEquals(CompassDirection.ESE, CompassDirection.E.nextClockwise());
        assertEquals(CompassDirection.SE, CompassDirection.ESE.nextClockwise());
        assertEquals(CompassDirection.SSE, CompassDirection.SE.nextClockwise());
        assertEquals(CompassDirection.S, CompassDirection.SSE.nextClockwise());
        assertEquals(CompassDirection.SSW, CompassDirection.S.nextClockwise());
        assertEquals(CompassDirection.SW, CompassDirection.SSW.nextClockwise());
        assertEquals(CompassDirection.WSW, CompassDirection.SW.nextClockwise());
        assertEquals(CompassDirection.W, CompassDirection.WSW.nextClockwise());
        assertEquals(CompassDirection.WNW, CompassDirection.W.nextClockwise());
        assertEquals(CompassDirection.NW, CompassDirection.WNW.nextClockwise());
        assertEquals(CompassDirection.NNW, CompassDirection.NW.nextClockwise());
        assertEquals(CompassDirection.N, CompassDirection.NNW.nextClockwise());
    }

    @Test
    void testNextCounterClockwise() {
        assertEquals(CompassDirection.NNW, CompassDirection.N.nextCounterClockwise());
        assertEquals(CompassDirection.NW, CompassDirection.NNW.nextCounterClockwise());
        assertEquals(CompassDirection.WNW, CompassDirection.NW.nextCounterClockwise());
        assertEquals(CompassDirection.W, CompassDirection.WNW.nextCounterClockwise());
        assertEquals(CompassDirection.WSW, CompassDirection.W.nextCounterClockwise());
        assertEquals(CompassDirection.SW, CompassDirection.WSW.nextCounterClockwise());
        assertEquals(CompassDirection.SSW, CompassDirection.SW.nextCounterClockwise());
        assertEquals(CompassDirection.S, CompassDirection.SSW.nextCounterClockwise());
        assertEquals(CompassDirection.SSE, CompassDirection.S.nextCounterClockwise());
        assertEquals(CompassDirection.SE, CompassDirection.SSE.nextCounterClockwise());
        assertEquals(CompassDirection.ESE, CompassDirection.SE.nextCounterClockwise());
        assertEquals(CompassDirection.E, CompassDirection.ESE.nextCounterClockwise());
        assertEquals(CompassDirection.ENE, CompassDirection.E.nextCounterClockwise());
        assertEquals(CompassDirection.NE, CompassDirection.ENE.nextCounterClockwise());
        assertEquals(CompassDirection.NNE, CompassDirection.NE.nextCounterClockwise());
        assertEquals(CompassDirection.N, CompassDirection.NNE.nextCounterClockwise());
    }

    @Test
    void testListClockwise() {
        var expectedClockwiseN = List.of(
                CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE,
                CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE,
                CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW,
                CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW
        );
        assertEquals(expectedClockwiseN, CompassDirection.N.listClockwise());

        var expectedClockwiseS = List.of(
                CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW,
                CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW,
                CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE,
                CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE
        );
        assertEquals(expectedClockwiseS, CompassDirection.S.listClockwise());
    }

    @Test
    void testListCounterClockwise() {
        var expectedCounterClockwiseN = List.of(
                CompassDirection.N, CompassDirection.NNW, CompassDirection.NW, CompassDirection.WNW,
                CompassDirection.W, CompassDirection.WSW, CompassDirection.SW, CompassDirection.SSW,
                CompassDirection.S, CompassDirection.SSE, CompassDirection.SE, CompassDirection.ESE,
                CompassDirection.E, CompassDirection.ENE, CompassDirection.NE, CompassDirection.NNE
        );
        assertEquals(expectedCounterClockwiseN, CompassDirection.N.listCounterClockwise());

        var expectedCounterClockwiseS = List.of(
                CompassDirection.S, CompassDirection.SSE, CompassDirection.SE, CompassDirection.ESE,
                CompassDirection.E, CompassDirection.ENE, CompassDirection.NE, CompassDirection.NNE,
                CompassDirection.N, CompassDirection.NNW, CompassDirection.NW, CompassDirection.WNW,
                CompassDirection.W, CompassDirection.WSW, CompassDirection.SW, CompassDirection.SSW
        );
        assertEquals(expectedCounterClockwiseS, CompassDirection.S.listCounterClockwise());
    }

    @Test
    void testStreamClockwise() {
        var expectedCounterClockwiseN = List.of(
                CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE,
                CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE,
                CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW,
                CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW
        );

        var actualClockwiseN = CompassDirection.N.streamClockwise().toList();
        assertEquals(expectedCounterClockwiseN, actualClockwiseN);

        var expectedClockwiseS = List.of(
                CompassDirection.S, CompassDirection.SSW, CompassDirection.SW, CompassDirection.WSW,
                CompassDirection.W, CompassDirection.WNW, CompassDirection.NW, CompassDirection.NNW,
                CompassDirection.N, CompassDirection.NNE, CompassDirection.NE, CompassDirection.ENE,
                CompassDirection.E, CompassDirection.ESE, CompassDirection.SE, CompassDirection.SSE
        );
        var actualClockwiseS = CompassDirection.S.streamClockwise().toList();
        assertEquals(expectedClockwiseS, actualClockwiseS);
    }

    @Test
    void testStreamCounterClockwise() {
        var expectedCounterClockwiseN = List.of(
                CompassDirection.N, CompassDirection.NNW, CompassDirection.NW, CompassDirection.WNW,
                CompassDirection.W, CompassDirection.WSW, CompassDirection.SW, CompassDirection.SSW,
                CompassDirection.S, CompassDirection.SSE, CompassDirection.SE, CompassDirection.ESE,
                CompassDirection.E, CompassDirection.ENE, CompassDirection.NE, CompassDirection.NNE
        );

        var actualCounterClockwiseN = CompassDirection.N.streamCounterClockwise().toList();
        assertEquals(expectedCounterClockwiseN, actualCounterClockwiseN);

        var expectedCounterClockwiseS = List.of(
                CompassDirection.S, CompassDirection.SSE, CompassDirection.SE, CompassDirection.ESE,
                CompassDirection.E, CompassDirection.ENE, CompassDirection.NE, CompassDirection.NNE,
                CompassDirection.N, CompassDirection.NNW, CompassDirection.NW, CompassDirection.WNW,
                CompassDirection.W, CompassDirection.WSW, CompassDirection.SW, CompassDirection.SSW
        );
        var actualCounterClockwiseS = CompassDirection.S.streamCounterClockwise().toList();
        assertEquals(expectedCounterClockwiseS, actualCounterClockwiseS);
    }

    @Test
    void testDistance() {
        // Same direction
        assertEquals(0, CompassDirection.distance(CompassDirection.N, CompassDirection.N));
        assertEquals(0, CompassDirection.distance(CompassDirection.E, CompassDirection.E));

        // Adjacent directions
        assertEquals(1, CompassDirection.distance(CompassDirection.N, CompassDirection.NNE));
        assertEquals(1, CompassDirection.distance(CompassDirection.NNE, CompassDirection.N));
        assertEquals(1, CompassDirection.distance(CompassDirection.S, CompassDirection.SSE));
        assertEquals(1, CompassDirection.distance(CompassDirection.SSE, CompassDirection.S));

        // Two steps
        assertEquals(2, CompassDirection.distance(CompassDirection.N, CompassDirection.NE));
        assertEquals(2, CompassDirection.distance(CompassDirection.NE, CompassDirection.N));
        assertEquals(2, CompassDirection.distance(CompassDirection.S, CompassDirection.SW));
        assertEquals(2, CompassDirection.distance(CompassDirection.SW, CompassDirection.S));

        // Opposite directions
        assertEquals(8, CompassDirection.distance(CompassDirection.N, CompassDirection.S));
        assertEquals(8, CompassDirection.distance(CompassDirection.S, CompassDirection.N));
        assertEquals(8, CompassDirection.distance(CompassDirection.E, CompassDirection.W));
        assertEquals(8, CompassDirection.distance(CompassDirection.W, CompassDirection.E));
        assertEquals(8, CompassDirection.distance(CompassDirection.NE, CompassDirection.SW));
        assertEquals(8, CompassDirection.distance(CompassDirection.SW, CompassDirection.NE));

        // Wrapping around (NNW to N)
        assertEquals(1, CompassDirection.distance(CompassDirection.NNW, CompassDirection.N));
        assertEquals(1, CompassDirection.distance(CompassDirection.N, CompassDirection.NNW));

        // Wrapping around longer distance
        assertEquals(3, CompassDirection.distance(CompassDirection.NNW, CompassDirection.NE));
        assertEquals(3, CompassDirection.distance(CompassDirection.NE, CompassDirection.NNW));
        assertEquals(7, CompassDirection.distance(CompassDirection.E, CompassDirection.WNW));
        assertEquals(7, CompassDirection.distance(CompassDirection.WNW, CompassDirection.E));

        // Symmetry: distance should be the same regardless of direction
        assertEquals(
                CompassDirection.distance(CompassDirection.N, CompassDirection.SE),
                CompassDirection.distance(CompassDirection.SE, CompassDirection.N)
        );
    }

    @Test
    void testDistanceOnRing() {
        // Test with 4-element ring (square edges only)
        List<CompassDirection> squareRing = List.of(
                CompassDirection.N,
                CompassDirection.E,
                CompassDirection.S,
                CompassDirection.W
        );

        // Same direction
        assertEquals(0, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.N, squareRing));
        assertEquals(0, CompassDirection.distanceOnRing(CompassDirection.E, CompassDirection.E, squareRing));

        // Adjacent in ring
        assertEquals(1, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.E, squareRing));
        assertEquals(1, CompassDirection.distanceOnRing(CompassDirection.E, CompassDirection.N, squareRing));
        assertEquals(1, CompassDirection.distanceOnRing(CompassDirection.E, CompassDirection.S, squareRing));
        assertEquals(1, CompassDirection.distanceOnRing(CompassDirection.S, CompassDirection.E, squareRing));

        // Opposite in 4-ring
        assertEquals(2, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.S, squareRing));
        assertEquals(2, CompassDirection.distanceOnRing(CompassDirection.S, CompassDirection.N, squareRing));
        assertEquals(2, CompassDirection.distanceOnRing(CompassDirection.E, CompassDirection.W, squareRing));
        assertEquals(2, CompassDirection.distanceOnRing(CompassDirection.W, CompassDirection.E, squareRing));

        // Wrapping around
        assertEquals(1, CompassDirection.distanceOnRing(CompassDirection.W, CompassDirection.N, squareRing));
        assertEquals(1, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.W, squareRing));

        // Test with 6-element ring (hexagon)
        List<CompassDirection> hexagonRing = List.of(
                CompassDirection.N,
                CompassDirection.NE,
                CompassDirection.SE,
                CompassDirection.S,
                CompassDirection.SW,
                CompassDirection.NW
        );

        assertEquals(0, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.N, hexagonRing));
        assertEquals(1, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.NE, hexagonRing));
        assertEquals(2, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.SE, hexagonRing));
        assertEquals(3, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.S, hexagonRing));
        assertEquals(2, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.SW, hexagonRing));
        assertEquals(1, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.NW, hexagonRing));

        // Test with 8-element ring (square edges and vertices)
        List<CompassDirection> squareFullRing = List.of(
                CompassDirection.N,
                CompassDirection.NE,
                CompassDirection.E,
                CompassDirection.SE,
                CompassDirection.S,
                CompassDirection.SW,
                CompassDirection.W,
                CompassDirection.NW
        );

        assertEquals(4, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.S, squareFullRing));
        assertEquals(2, CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.E, squareFullRing));
    }

    @Test
    void testDistanceOnRingWithMissingDirection() {
        List<CompassDirection> squareRing = List.of(
                CompassDirection.N,
                CompassDirection.E,
                CompassDirection.S,
                CompassDirection.W
        );

        assertThrows(IllegalArgumentException.class, () -> CompassDirection.distanceOnRing(CompassDirection.NNE, CompassDirection.N, squareRing));
        assertThrows(IllegalArgumentException.class, () -> CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.NNE, squareRing));
        assertThrows(IllegalArgumentException.class, () -> CompassDirection.distanceOnRing(CompassDirection.E, CompassDirection.ENE, squareRing));
    }

    @Test
    void testDistanceOnRingWithEmptyRing() {
        assertThrows(IllegalArgumentException.class, () -> CompassDirection.distanceOnRing(CompassDirection.N, CompassDirection.E, List.of()));
    }

}
