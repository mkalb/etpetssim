package de.mkalb.etpetssim.simulations.core.viewmodel;

/**
 * Declares how a user action is applied in the simulation UI.
 */
public enum SimulationUserActionScope {

    /**
     * Action applies to the currently selected grid cell.
     */
    SELECTED_CELL,

    /**
     * Action applies to global simulation state and does not require a selected cell.
     */
    GLOBAL

}
