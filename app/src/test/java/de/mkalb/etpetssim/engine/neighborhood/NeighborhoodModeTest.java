package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
final class NeighborhoodModeTest {

    @Test
    void testEnumValues() {
        assertNotNull(NeighborhoodMode.valueOf("EDGES_ONLY"));
        assertNotNull(NeighborhoodMode.valueOf("EDGES_AND_VERTICES"));
    }

    @Test
    void testEnumCount() {
        assertEquals(2, NeighborhoodMode.values().length, "There should be exactly 2 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, NeighborhoodMode.EDGES_ONLY.ordinal());
        assertEquals(1, NeighborhoodMode.EDGES_AND_VERTICES.ordinal());
    }

    @Test
    void testResourceKey() {
        assertEquals("neighborhoodmode.edgesonly", NeighborhoodMode.EDGES_ONLY.resourceKey());
        assertEquals("neighborhoodmode.edgesandvertices", NeighborhoodMode.EDGES_AND_VERTICES.resourceKey());
    }

    @Test
    void testLabelResourceKey() {
        assertEquals("neighborhoodmode.label", NeighborhoodMode.labelResourceKey());
    }

    @Test
    void testIsVertexNeighborIncluded() {
        assertFalse(NeighborhoodMode.EDGES_ONLY.isVertexNeighborIncluded());
        assertTrue(NeighborhoodMode.EDGES_AND_VERTICES.isVertexNeighborIncluded());
    }

}
