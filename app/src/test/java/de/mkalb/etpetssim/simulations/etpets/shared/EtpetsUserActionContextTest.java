package de.mkalb.etpetssim.simulations.etpets.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class EtpetsUserActionContextTest {

    @Test
    void testSetTerrainRecord() {
        EtpetsUserActionContext.SetTerrain context = new EtpetsUserActionContext.SetTerrain(EtpetsTerrainChoice.ROCK);

        assertAll(
                () -> assertSame(EtpetsTerrainChoice.ROCK, context.terrainChoice()),
                () -> assertTrue(context.toString().contains("terrainChoice=ROCK")));
    }

    @Test
    void testSetResourceRecord() {
        EtpetsUserActionContext.SetResource context = new EtpetsUserActionContext.SetResource(EtpetsResourceChoice.PLANT);

        assertAll(
                () -> assertSame(EtpetsResourceChoice.PLANT, context.resourceChoice()),
                () -> assertTrue(context.toString().contains("resourceChoice=PLANT")));
    }

}
