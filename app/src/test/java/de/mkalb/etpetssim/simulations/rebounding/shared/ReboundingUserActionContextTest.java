package de.mkalb.etpetssim.simulations.rebounding.shared;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ReboundingUserActionContextTest {

    @Test
    void testPermittedSubclasses() {
        assertArrayEquals(
                new Class<?>[]{
                        ReboundingUserActionContext.FixedAction.class,
                        ReboundingUserActionContext.AddRebounder.class
                },
                ReboundingUserActionContext.class.getPermittedSubclasses());
    }

    @Test
    void testFixedActionDeclarationOrder() {
        assertArrayEquals(
                new ReboundingUserActionContext.FixedAction[]{
                        ReboundingUserActionContext.FixedAction.ADD_WALL,
                        ReboundingUserActionContext.FixedAction.FILL_WALLS,
                        ReboundingUserActionContext.FixedAction.REMOVE_WALL,
                        ReboundingUserActionContext.FixedAction.REMOVE_REBOUNDER
                },
                ReboundingUserActionContext.FixedAction.values());
    }

    @Test
    void testAddRebounderStoresDirection() {
        ReboundingUserActionContext.AddRebounder context = new ReboundingUserActionContext.AddRebounder(CompassDirection.SE);

        assertAll(
                () -> assertInstanceOf(SimulationUserActionContext.class, context),
                () -> assertSame(CompassDirection.SE, context.direction())
        );
    }

}
