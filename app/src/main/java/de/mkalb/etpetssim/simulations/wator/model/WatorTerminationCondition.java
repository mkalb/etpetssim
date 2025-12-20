package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;

public final class WatorTerminationCondition implements SimulationTerminationCondition<WatorEntity,
        ReadableGridModel<WatorEntity>, WatorStatistics> {

    private static final double FISH_MAX_PERCENTAGE = 0.9d;

    @Override
    public boolean isFinished(ReadableGridModel<WatorEntity> model, int stepCount, WatorStatistics statistics) {
        return (statistics.getSharkCells() == 0)
                && ((statistics.getFishCells() == 0)
                || (statistics.getFishCells() > (statistics.getTotalCells() * FISH_MAX_PERCENTAGE)));
    }

}
