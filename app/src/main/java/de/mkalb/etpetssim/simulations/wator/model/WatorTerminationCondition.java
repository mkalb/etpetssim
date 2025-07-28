package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;

public final class WatorTerminationCondition implements SimulationTerminationCondition<WatorEntity, WatorStatistics> {

    @Override
    public boolean isFinished(GridModel<WatorEntity> model, long step, WatorStatistics statistics) {
        return statistics.getSharkCells() == 0;
    }

}
