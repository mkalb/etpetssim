package de.mkalb.etpetssim.simulations.wator.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies which Wa-Tor edit action should be applied to the selected cell.
 */
public enum WatorUserActionContext implements SimulationUserActionContext {
    /**
     * Add a fish to the selected cell.
     */
    ADD_FISH,

    /**
     * Add a shark to the selected cell.
     */
    ADD_SHARK,

    /**
     * Remove a fish or shark from the selected cell.
     */
    REMOVE_CREATURE
}
