package de.mkalb.etpetssim.simulations.etpets.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class EtpetsUserActionContextTest {

    @Test
    void testPermittedSubclasses() {
        assertArrayEquals(
                new Class<?>[]{
                        EtpetsUserActionContext.SetTerrain.class,
                        EtpetsUserActionContext.SetResource.class
                },
                EtpetsUserActionContext.class.getPermittedSubclasses());
    }

    @Test
    void testSetTerrainStoresTerrainChoice() {
        EtpetsUserActionContext.SetTerrain context = new EtpetsUserActionContext.SetTerrain(EtpetsTerrainChoice.ROCK);

        assertAll(
                () -> assertInstanceOf(SimulationUserActionContext.class, context),
                () -> assertSame(EtpetsTerrainChoice.ROCK, context.terrainChoice())
        );
    }

    @Test
    void testSetResourceStoresResourceChoice() {
        EtpetsUserActionContext.SetResource context = new EtpetsUserActionContext.SetResource(EtpetsResourceChoice.PLANT);

        assertAll(
                () -> assertInstanceOf(SimulationUserActionContext.class, context),
                () -> assertSame(EtpetsResourceChoice.PLANT, context.resourceChoice())
        );
    }

}
