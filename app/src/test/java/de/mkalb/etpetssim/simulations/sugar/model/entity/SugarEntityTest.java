package de.mkalb.etpetssim.simulations.sugar.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SugarEntityTest {

    @Test
    void testIsNotEmpty() {
        assertAll(
                () -> assertTrue(new Sugar(4, 2).isNotEmpty()),
                () -> assertTrue(new Agent(10, 0).isNotEmpty()),
                () -> assertFalse(NoResource.NO_RESOURCE.isNotEmpty()),
                () -> assertFalse(NoAgent.NO_AGENT.isNotEmpty())
        );
    }

}
