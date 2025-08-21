package de.mkalb.etpetssim.engine.neighborhood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
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
    void testEnumOrdinal() {
        assertEquals(0, CellConnectionType.EDGE.ordinal());
        assertEquals(1, CellConnectionType.VERTEX.ordinal());
    }

    @Test
    void testResourceKey() {
        assertEquals("cellconnectiontype.edge", CellConnectionType.EDGE.resourceKey());
        assertEquals("cellconnectiontype.vertex", CellConnectionType.VERTEX.resourceKey());
    }

    @Test
    void testLabelResourceKey() {
        assertEquals("cellconnectiontype.label", CellConnectionType.labelResourceKey());
    }

}
