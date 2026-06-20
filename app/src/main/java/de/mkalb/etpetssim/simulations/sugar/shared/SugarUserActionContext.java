package de.mkalb.etpetssim.simulations.sugar.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies which edit action should be applied to the selected Sugar cell.
 */
@SuppressWarnings("MarkerInterface")
public sealed interface SugarUserActionContext extends SimulationUserActionContext
        permits SugarUserActionContext.FixedAction, SugarUserActionContext.AddSugar {

    /**
     * Fixed actions that do not require additional context parameters.
     */
    enum FixedAction implements SugarUserActionContext {
        REMOVE_SUGAR
    }

    /**
     * Parameterized context for adding sugar with a selected amount level.
     *
     * @param level selected sugar amount level
     */
    record AddSugar(SugarAddSugarLevel level) implements SugarUserActionContext {
    }

}
