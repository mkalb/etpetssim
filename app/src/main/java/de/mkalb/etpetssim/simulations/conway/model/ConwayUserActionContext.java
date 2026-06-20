package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies which Conway edit action should be applied.
 */
@SuppressWarnings("MarkerInterface")
public sealed interface ConwayUserActionContext extends SimulationUserActionContext
        permits ConwayUserActionContext.FixedAction, ConwayUserActionContext.PlacePattern {

    /**
     * Fixed actions that do not require additional parameters.
     */
    enum FixedAction implements ConwayUserActionContext {
        CLEAR_GRID,
        TOGGLE_CELL
    }

    /**
     * Parameterized context for placing a selected pattern.
     *
     * @param patternChoice selected pattern
     */
    record PlacePattern(ConwayPatternChoice patternChoice) implements ConwayUserActionContext {
    }

}
