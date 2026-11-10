package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

final class TerrainEntityTest {

    @Test
    void testIsEmpty() {
        assertFalse(TerrainConstant.GROUND.isEmpty());
    }

}
