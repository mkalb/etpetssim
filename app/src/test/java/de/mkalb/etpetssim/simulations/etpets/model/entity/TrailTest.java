package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class TrailTest {

    @Test
    void testToDisplayString() {
        Trail trail = new Trail(3);

        assertEquals("[TRAIL I=3]", trail.toDisplayString());
    }

}
