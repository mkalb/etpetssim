package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EtpetsEntityTest {

    @Test
    void testIsNotEmpty() {
        assertAll(
                () -> assertTrue(TerrainConstant.GROUND.isNotEmpty()),
                () -> assertFalse(NoResource.NO_RESOURCE.isNotEmpty()),
                () -> assertFalse(NoAgent.NO_AGENT.isNotEmpty())
        );
    }

}
