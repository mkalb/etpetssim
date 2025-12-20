package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;

public final class ForestTerminationCondition
        implements SimulationTerminationCondition<ForestEntity, ReadableGridModel<ForestEntity>, ForestStatistics> {

    @Override
    public boolean isFinished(ReadableGridModel<ForestEntity> model, int stepCount, ForestStatistics statistics) {
        return (statistics.getTreeCells() == 0) && (statistics.getBurningCells() == 0);
    }

}
