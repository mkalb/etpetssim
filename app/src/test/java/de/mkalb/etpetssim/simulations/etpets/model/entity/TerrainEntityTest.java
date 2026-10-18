package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class TerrainEntityTest {

    @Test
    void testIsTerrain() {
        assertTrue(TerrainConstant.GROUND.isTerrain());
    }

    @Test
    void testIsResource() {
        assertFalse(TerrainConstant.GROUND.isResource());
    }

    @Test
    void testIsAgent() {
        assertFalse(TerrainConstant.GROUND.isAgent());
    }

    @Test
    void testIsEmpty() {
        assertFalse(TerrainConstant.GROUND.isEmpty());
    }

}
