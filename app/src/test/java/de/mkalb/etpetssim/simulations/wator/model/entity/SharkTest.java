package de.mkalb.etpetssim.simulations.wator.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("MagicNumber")
final class SharkTest {

    @Test
    void testToDisplayString() {
        Shark shark = new Shark(7L, 3, 20);

        assertEquals("[SHARK #7 *3 E=20]", shark.toDisplayString());
    }

}
