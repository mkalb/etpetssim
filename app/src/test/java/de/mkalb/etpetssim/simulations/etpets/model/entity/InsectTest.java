package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("MagicNumber")
final class InsectTest {

    @Test
    void testToDisplayString() {
        Insect insect = new Insect(2.0d, 5.0d, 0.25d);

        assertEquals("[INSECT A=2.0/5.0 R=0.25]", insect.toDisplayString());
    }

}
