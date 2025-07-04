package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellShapeTest {

    @Test
    void testEnumValues() {
        assertNotNull(CellShape.valueOf("TRIANGLE"));
        assertNotNull(CellShape.valueOf("SQUARE"));
        assertNotNull(CellShape.valueOf("HEXAGON"));
    }

    @Test
    void testEnumCount() {
        assertEquals(3, CellShape.values().length, "There should be exactly 3 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, CellShape.TRIANGLE.ordinal());
        assertEquals(1, CellShape.SQUARE.ordinal());
        assertEquals(2, CellShape.HEXAGON.ordinal());
    }

    @Test
    void testVertexCounts() {
        assertEquals(3, CellShape.TRIANGLE.vertexCount(), "TRIANGLE should have 3 vertices");
        assertEquals(4, CellShape.SQUARE.vertexCount(), "SQUARE should have 4 vertices");
        assertEquals(6, CellShape.HEXAGON.vertexCount(), "HEXAGON should have 6 vertices");
    }

    @Test
    void testResourceKey() {
        assertEquals("cellshape.triangle", CellShape.TRIANGLE.resourceKey(), "TRIANGLE should have the correct resource key");
        assertEquals("cellshape.square", CellShape.SQUARE.resourceKey(), "SQUARE should have the correct resource key");
        assertEquals("cellshape.hexagon", CellShape.HEXAGON.resourceKey(), "HEXAGON should have the correct resource key");
    }

    @Test
    void testLabelResourceKey() {
        assertEquals("cellshape.label", CellShape.labelResourceKey(), "CellShape should have the correct resource key");
    }

}
