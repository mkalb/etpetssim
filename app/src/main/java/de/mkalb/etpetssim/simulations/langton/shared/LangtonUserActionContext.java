package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies which Langton edit action should be applied to the selected cell.
 */
@SuppressWarnings("MarkerInterface")
public sealed interface LangtonUserActionContext extends SimulationUserActionContext
        permits LangtonUserActionContext.FixedAction, LangtonUserActionContext.AddAnt {

    /**
     * Fixed actions that do not require additional parameters.
     */
    enum FixedAction implements LangtonUserActionContext {
        REMOVE_ANT
    }

    /**
     * Parameterized context for adding an ant with a selected initial direction.
     *
     * @param direction selected initial direction
     */
    record AddAnt(CompassDirection direction) implements LangtonUserActionContext {
    }

}
