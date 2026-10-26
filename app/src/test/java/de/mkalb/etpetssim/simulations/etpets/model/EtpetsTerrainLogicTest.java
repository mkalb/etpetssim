package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import org.junit.jupiter.api.Test;

import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsLogicTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

final class EtpetsTerrainLogicTest {

    @Test
    void testApply() {
        EtpetsGridModel model = createGridModel();
        Trail freeTrail = new Trail(10);
        Trail occupiedTrail = new Trail(10);
        GridCoordinate fadingCoordinate = new GridCoordinate(2, 0);
        GridCoordinate rockCoordinate = new GridCoordinate(3, 0);
        model.terrainModel().setEntity(new GridCoordinate(0, 0), freeTrail);
        model.terrainModel().setEntity(new GridCoordinate(1, 0), occupiedTrail);
        model.terrainModel().setEntity(fadingCoordinate, new Trail(EtpetsBalance.TRAIL_INTENSITY_MIN));
        model.terrainModel().setEntity(rockCoordinate, TerrainConstant.ROCK);
        model.agentModel().setEntity(new GridCoordinate(1, 0), createPet(0, EtpetsBalance.PET_CURRENT_ENERGY_MAX));

        EtpetsTerrainLogic.apply(model);

        assertAll(
                () -> assertEquals(10 - EtpetsBalance.TRAIL_INTENSITY_DECAY_PER_STEP, freeTrail.intensity()),
                () -> assertEquals(10, occupiedTrail.intensity()),
                () -> assertSame(TerrainConstant.GROUND, model.terrainModel().getEntity(fadingCoordinate)),
                () -> assertSame(TerrainConstant.ROCK, model.terrainModel().getEntity(rockCoordinate))
        );
    }

}
