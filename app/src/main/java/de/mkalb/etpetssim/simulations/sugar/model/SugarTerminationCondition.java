package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;

public final class SugarTerminationCondition
        implements
        SimulationTerminationCondition<SugarEntity, SugarGridModel, SugarStatistics> {

    @Override
    public boolean isFinished(SugarGridModel model, int stepCount, SugarStatistics statistics) {
        return statistics.getAgentCells() == 0;
    }

}
