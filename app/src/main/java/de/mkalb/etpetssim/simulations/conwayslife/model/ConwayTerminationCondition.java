package de.mkalb.etpetssim.simulations.conwayslife.model;

import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;

public class ConwayTerminationCondition implements SimulationTerminationCondition<ConwayEntity> {

    @Override
    public boolean isFinished(GridModel<ConwayEntity> model, long step) {
        return false;
    }

}