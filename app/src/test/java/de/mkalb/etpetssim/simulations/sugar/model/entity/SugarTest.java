package de.mkalb.etpetssim.simulations.sugar.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SugarTest {

    @Test
    void testToDisplayString() {
        Sugar sugar = new Sugar(4, 3);

        assertEquals("[SUGAR A=3/4]", sugar.toDisplayString());
    }

}
