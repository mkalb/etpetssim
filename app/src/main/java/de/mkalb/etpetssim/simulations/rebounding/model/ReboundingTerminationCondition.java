package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;

public final class ReboundingTerminationCondition implements SimulationTerminationCondition<ReboundingEntity,
        ReadableGridModel<ReboundingEntity>, ReboundingStatistics> {

    /**
     * Checks whether the rebounding simulation has finished.
     * <p>
     * The simulation terminates once at most one entity-occupied cell remains,
     * i.e. when {@code statistics.getMovingEntityCells() <= 1}.
     *
     * @param model the current simulation model
     * @param stepCount the number of simulation steps completed
     * @param statistics the current rebounding statistics
     * @return {@code true} if the simulation should terminate, otherwise {@code false}
     */
    @Override
    public boolean isFinished(ReadableGridModel<ReboundingEntity> model, int stepCount, ReboundingStatistics statistics) {
        return (statistics.getMovingEntityCells() <= 1);
    }

}
