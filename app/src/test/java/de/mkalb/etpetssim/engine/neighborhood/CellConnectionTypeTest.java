package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class CellConnectionTypeTest {

    @Test
    void testEnumValues() {
        assertNotNull(CellConnectionType.valueOf("EDGE"));
        assertNotNull(CellConnectionType.valueOf("VERTEX"));
    }

    @Test
    void testEnumCount() {
        assertEquals(2, CellConnectionType.values().length, "There should be exactly 2 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new CellConnectionType[]{CellConnectionType.EDGE, CellConnectionType.VERTEX},
                CellConnectionType.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> CellConnectionType.valueOf("INVALID"));
    }

    @Test
    void testStaticLabelResourceKey() {
        assertEquals("cellconnectiontype.label", CellConnectionType.labelResourceKey());
    }

    @Test
    void testResourceKey() {
        assertEquals("cellconnectiontype.edge", CellConnectionType.EDGE.resourceKey());
        assertEquals("cellconnectiontype.vertex", CellConnectionType.VERTEX.resourceKey());
    }

}
