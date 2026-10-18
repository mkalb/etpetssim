package de.mkalb.etpetssim.simulations.sugar.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

final class SugarUserActionContextTest {

    @Test
    void testAddSugarStoresLevel() {
        SugarUserActionContext.AddSugar context = new SugarUserActionContext.AddSugar(SugarAddSugarLevel.HIGH);

        assertSame(SugarAddSugarLevel.HIGH, context.level());
    }

}
