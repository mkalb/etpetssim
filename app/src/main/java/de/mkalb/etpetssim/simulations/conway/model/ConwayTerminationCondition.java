package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;

public final class ConwayTerminationCondition
        implements SimulationTerminationCondition<ConwayEntity, ConwayStatistics> {

    @Override
    public boolean isFinished(GridModel<ConwayEntity> model, long step, ConwayStatistics statistics) {
        return statistics.getAliveCells() == 0;
    }

}
