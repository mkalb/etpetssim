package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;

public class ConwayTerminationCondition implements SimulationTerminationCondition<ConwayEntity> {

    @Override
    public boolean isFinished(GridModel<ConwayEntity> model, long step) {
        return model.structure().coordinatesStream()
                    .noneMatch(coordinate -> model.getEntity(coordinate).isAlive());
    }

}