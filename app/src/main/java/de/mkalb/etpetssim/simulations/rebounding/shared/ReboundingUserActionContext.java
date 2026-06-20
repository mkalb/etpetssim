package de.mkalb.etpetssim.simulations.rebounding.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies the fixed edit action requested by the user in the Rebounding simulation.
 */
public enum ReboundingUserActionContext implements SimulationUserActionContext {
    ADD_WALL,
    REMOVE_WALL,
    REMOVE_REBOUNDER
}
