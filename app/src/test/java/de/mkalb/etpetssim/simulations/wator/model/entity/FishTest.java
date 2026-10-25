package de.mkalb.etpetssim.simulations.wator.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("MagicNumber")
final class FishTest {

    @Test
    void testToDisplayString() {
        Fish fish = new Fish(7L, 3);

        assertEquals("[FISH #7 *3]", fish.toDisplayString());
    }

}
