package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import org.junit.jupiter.api.Test;

import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsLogicTestSupport.createGridModel;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class EtpetsResourceLogicTest {

    private static final double DELTA = 1.0e-9d;

    @Test
    void testApply() {
        EtpetsGridModel model = createGridModel();
        Plant plant = new Plant(1.0d, 10.0d, 2.0d);
        Insect insect = new Insect(9.5d, 10.0d, 2.0d);
        model.resourceModel().setEntity(new GridCoordinate(0, 0), plant);
        model.resourceModel().setEntity(new GridCoordinate(1, 0), insect);

        EtpetsResourceLogic.apply(model);

        assertAll(
                () -> assertEquals(3.0d, plant.currentAmount(), DELTA),
                () -> assertEquals(10.0d, insect.currentAmount(), DELTA)
        );
    }

}
