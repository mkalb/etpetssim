package de.mkalb.etpetssim.simulations.wator.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies which Wa-Tor edit action should be applied.
 */
public enum WatorUserActionContext implements SimulationUserActionContext {

    /**
     * Adds a fish to the selected cell.
     */
    ADD_FISH,

    /**
     * Adds a shark to the selected cell.
     */
    ADD_SHARK,

    /**
     * Removes a fish or shark from the selected cell.
     */
    REMOVE_CREATURE

}
