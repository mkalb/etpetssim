package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

@SuppressWarnings("MarkerInterface")
public sealed interface ConwayUserActionContext extends SimulationUserActionContext
        permits ConwayUserActionContext.FixedAction, ConwayUserActionContext.PlacePattern {

    enum FixedAction implements ConwayUserActionContext {
        TOGGLE_CELL
    }

    record PlacePattern(ConwayPatternChoice patternChoice) implements ConwayUserActionContext {
    }

}

