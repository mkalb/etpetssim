package de.mkalb.etpetssim.simulations.wator.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies which type of creature the user wants to add to the selected cell.
 */
public enum WatorUserActionContext implements SimulationUserActionContext {
    /**
     * Add a fish to the selected cell.
     */
    ADD_FISH,

    /**
     * Add a shark to the selected cell.
     */
    ADD_SHARK
}
