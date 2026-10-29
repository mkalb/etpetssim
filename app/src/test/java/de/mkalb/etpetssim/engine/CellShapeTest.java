package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.assertAll;
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
        assertAll(
                () -> assertEquals(3, CellShape.TRIANGLE.vertexCount()),
                () -> assertEquals(4, CellShape.SQUARE.vertexCount()),
                () -> assertEquals(6, CellShape.HEXAGON.vertexCount())
        );
    }

}
