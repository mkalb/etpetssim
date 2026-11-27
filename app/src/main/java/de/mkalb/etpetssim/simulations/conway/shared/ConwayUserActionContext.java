package de.mkalb.etpetssim.simulations.conway.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Edit action context of Conway's Game of Life that identifies which edit action should be applied.
 */
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
