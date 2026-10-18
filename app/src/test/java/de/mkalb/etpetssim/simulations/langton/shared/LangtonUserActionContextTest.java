package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class LangtonUserActionContextTest {

    @Test
    void testAddAntRecord() {
        LangtonUserActionContext.AddAnt context = new LangtonUserActionContext.AddAnt(CompassDirection.W);

        assertAll(
                () -> assertSame(CompassDirection.W, context.direction()),
                () -> assertTrue(context.toString().contains("direction=W")));
    }

}
