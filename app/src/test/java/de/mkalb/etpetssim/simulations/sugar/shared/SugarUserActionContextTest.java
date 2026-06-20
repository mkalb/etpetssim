package de.mkalb.etpetssim.simulations.sugar.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SugarUserActionContextTest {

    @Test
    void testPermittedSubclasses() {
        assertArrayEquals(
                new Class<?>[]{
                        SugarUserActionContext.FixedAction.class,
                        SugarUserActionContext.AddSugar.class
                },
                SugarUserActionContext.class.getPermittedSubclasses());
    }

    @Test
    void testFixedActionDeclarationOrder() {
        assertArrayEquals(
                new SugarUserActionContext.FixedAction[]{
                        SugarUserActionContext.FixedAction.REMOVE_SUGAR
                },
                SugarUserActionContext.FixedAction.values());
    }

    @Test
    void testAddSugarStoresLevel() {
        SugarUserActionContext.AddSugar context = new SugarUserActionContext.AddSugar(SugarAddSugarLevel.HIGH);

        assertAll(
                () -> assertInstanceOf(SimulationUserActionContext.class, context),
                () -> assertSame(SugarAddSugarLevel.HIGH, context.level())
        );
    }

}
