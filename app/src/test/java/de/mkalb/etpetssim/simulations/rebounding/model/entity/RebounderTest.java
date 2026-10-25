package de.mkalb.etpetssim.simulations.rebounding.model.entity;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class RebounderTest {

    @Test
    void testToDisplayString() {
        Rebounder rebounder = new Rebounder(CompassDirection.NE);

        assertEquals("[REBOUNDER " + CompassDirection.NE.arrow() + "]", rebounder.toDisplayString());
    }

}
