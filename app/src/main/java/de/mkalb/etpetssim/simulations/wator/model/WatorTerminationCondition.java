package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;

public final class WatorTerminationCondition implements SimulationTerminationCondition<WatorEntity, WatorStatistics> {

    private static final double FISH_MAX_PERCENTAGE = 0.9d;

    @Override
    public boolean isFinished(GridModel<WatorEntity> model, long step, WatorStatistics statistics) {
        return (statistics.getSharkCells() == 0)
                && ((statistics.getFishCells() == 0)
                || (statistics.getFishCells() > (statistics.getTotalCells() * FISH_MAX_PERCENTAGE)));
    }

}
