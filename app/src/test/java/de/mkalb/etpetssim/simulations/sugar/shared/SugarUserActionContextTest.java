package de.mkalb.etpetssim.simulations.sugar.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SugarUserActionContextTest {

    @Test
    void testAddSugarRecord() {
        SugarUserActionContext.AddSugar context = new SugarUserActionContext.AddSugar(SugarAddSugarLevel.HIGH);

        assertAll(
                () -> assertSame(SugarAddSugarLevel.HIGH, context.level()),
                () -> assertTrue(context.toString().contains("level=HIGH")));
    }

}
