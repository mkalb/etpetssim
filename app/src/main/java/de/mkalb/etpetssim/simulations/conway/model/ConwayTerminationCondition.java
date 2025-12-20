package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;

public final class ConwayTerminationCondition
        implements SimulationTerminationCondition<ConwayEntity, ReadableGridModel<ConwayEntity>, ConwayStatistics> {

    @Override
    public boolean isFinished(ReadableGridModel<ConwayEntity> model, int stepCount, ConwayStatistics statistics) {
        return (statistics.getAliveCells() == 0) || (statistics.getChangedCells() == 0);
    }

}
