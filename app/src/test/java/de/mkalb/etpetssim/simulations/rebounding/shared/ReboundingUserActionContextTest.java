package de.mkalb.etpetssim.simulations.rebounding.shared;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

final class ReboundingUserActionContextTest {

    @Test
    void testAddRebounderStoresDirection() {
        ReboundingUserActionContext.AddRebounder context = new ReboundingUserActionContext.AddRebounder(CompassDirection.SE);

        assertSame(CompassDirection.SE, context.direction());
    }

}
