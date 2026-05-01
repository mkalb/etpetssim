package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void testDeclarationOrder() {
        assertArrayEquals(
                new NeighborhoodMode[]{NeighborhoodMode.EDGES_ONLY, NeighborhoodMode.EDGES_AND_VERTICES},
                NeighborhoodMode.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> NeighborhoodMode.valueOf("INVALID"));
    }

    @Test
    void testStaticLabelResourceKey() {
        assertEquals("neighborhoodmode.label", NeighborhoodMode.labelResourceKey());
    }

    @Test
    void testIncludesVertexNeighbors() {
        assertFalse(NeighborhoodMode.EDGES_ONLY.includesVertexNeighbors());
        assertTrue(NeighborhoodMode.EDGES_AND_VERTICES.includesVertexNeighbors());
    }

    @Test
    void testResourceKey() {
        assertEquals("neighborhoodmode.edgesonly", NeighborhoodMode.EDGES_ONLY.resourceKey());
        assertEquals("neighborhoodmode.edgesandvertices", NeighborhoodMode.EDGES_AND_VERTICES.resourceKey());
    }

}
