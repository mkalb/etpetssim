package de.mkalb.etpetssim.simulations.rebounding.shared;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies which edit action should be applied to the selected Rebounding cell.
 */
@SuppressWarnings("MarkerInterface")
public sealed interface ReboundingUserActionContext extends SimulationUserActionContext
        permits ReboundingUserActionContext.FixedAction, ReboundingUserActionContext.AddRebounder {

    /**
     * Fixed actions that do not require additional parameters.
     */
    enum FixedAction implements ReboundingUserActionContext {
        ADD_WALL,
        FILL_WALLS,
        REMOVE_WALL,
        REMOVE_REBOUNDER
    }

    /**
     * Parameterized context for adding a rebounder with a selected direction.
     *
     * @param direction selected movement direction
     */
    record AddRebounder(CompassDirection direction) implements ReboundingUserActionContext {
    }

}
