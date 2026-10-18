package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

final class LangtonUserActionContextTest {

    @Test
    void testAddAntStoresDirection() {
        LangtonUserActionContext.AddAnt context = new LangtonUserActionContext.AddAnt(CompassDirection.W);

        assertSame(CompassDirection.W, context.direction());
    }

}
