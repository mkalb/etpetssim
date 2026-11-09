package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.executor.SimulationTerminationCondition;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;

/**
 * Termination condition of Rebounding that ends the simulation when at most one rebounder is left.
 */
public final class ReboundingTerminationCondition implements SimulationTerminationCondition<ReboundingEntity,
        ReadableGridModel<ReboundingEntity>, ReboundingStatistics> {

    @Override
    public boolean isFinished(ReadableGridModel<ReboundingEntity> model,
                              int stepCount,
                              ReboundingStatistics statistics) {
        return statistics.rebounderCells() <= 1;
    }

}
