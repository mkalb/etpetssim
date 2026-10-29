package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class CellShapeTest {

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(CellShape.labelResourceKey()));
    }

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(Arrays.stream(CellShape.values()).map(CellShape::resourceKey).toList());
    }

    @Test
    void testVertexCount() {
        assertEquals(3, CellShape.TRIANGLE.vertexCount(), "TRIANGLE should have 3 vertices");
        assertEquals(4, CellShape.SQUARE.vertexCount(), "SQUARE should have 4 vertices");
        assertEquals(6, CellShape.HEXAGON.vertexCount(), "HEXAGON should have 6 vertices");
    }

}
