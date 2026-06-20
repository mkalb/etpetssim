package de.mkalb.etpetssim.simulations.etpets.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies which ET Pets edit action should be applied to the selected cell.
 */
@SuppressWarnings("MarkerInterface")
public sealed interface EtpetsUserActionContext extends SimulationUserActionContext
        permits EtpetsUserActionContext.SetTerrain, EtpetsUserActionContext.SetResource {

    /**
     * Parameterized context for setting terrain.
     *
     * @param terrainChoice selected terrain
     */
    record SetTerrain(EtpetsTerrainChoice terrainChoice) implements EtpetsUserActionContext {
    }

    /**
     * Parameterized context for setting resources.
     *
     * @param resourceChoice selected resource
     */
    record SetResource(EtpetsResourceChoice resourceChoice) implements EtpetsUserActionContext {
    }

}
