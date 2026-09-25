package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.executor.SimulationTerminationCondition;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;

public final class WatorTerminationCondition implements SimulationTerminationCondition<WatorEntity,
        ReadableGridModel<WatorEntity>, WatorStatistics> {

    @Override
    public boolean isFinished(ReadableGridModel<WatorEntity> model, int stepCount, WatorStatistics statistics) {
        return (statistics.sharkCells() == 0)
                && ((statistics.fishCells() == 0)
                || (statistics.fishCells() > (statistics.totalCells() * WatorBalance.TERMINATION_FISH_MAX_SHARE)));
    }

}
