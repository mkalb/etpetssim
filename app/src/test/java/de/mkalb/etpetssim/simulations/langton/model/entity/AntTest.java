package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class AntTest {

    @Test
    void testToDisplayString() {
        Ant ant = new Ant(CompassDirection.N);

        assertEquals("[ANT " + CompassDirection.N.arrow() + "]", ant.toDisplayString());
    }

}
