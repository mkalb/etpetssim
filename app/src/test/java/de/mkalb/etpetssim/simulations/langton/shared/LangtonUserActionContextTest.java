package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class LangtonUserActionContextTest {

    @Test
    void testPermittedSubclasses() {
        assertArrayEquals(
                new Class<?>[]{
                        LangtonUserActionContext.FixedAction.class,
                        LangtonUserActionContext.AddAnt.class
                },
                LangtonUserActionContext.class.getPermittedSubclasses());
    }

    @Test
    void testFixedActionDeclarationOrder() {
        assertArrayEquals(
                new LangtonUserActionContext.FixedAction[]{
                        LangtonUserActionContext.FixedAction.REMOVE_ANT
                },
                LangtonUserActionContext.FixedAction.values());
    }

    @Test
    void testAddAntStoresDirection() {
        LangtonUserActionContext.AddAnt context = new LangtonUserActionContext.AddAnt(CompassDirection.W);

        assertAll(
                () -> assertInstanceOf(SimulationUserActionContext.class, context),
                () -> assertSame(CompassDirection.W, context.direction())
        );
    }

}
