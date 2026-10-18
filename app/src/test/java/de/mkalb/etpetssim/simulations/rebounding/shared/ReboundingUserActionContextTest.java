package de.mkalb.etpetssim.simulations.rebounding.shared;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ReboundingUserActionContextTest {

    @Test
    void testAddRebounderRecord() {
        ReboundingUserActionContext.AddRebounder context = new ReboundingUserActionContext.AddRebounder(CompassDirection.SE);

        assertAll(
                () -> assertSame(CompassDirection.SE, context.direction()),
                () -> assertTrue(context.toString().contains("direction=SE")));
    }

}
