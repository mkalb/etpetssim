package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.LayeredCompositeGridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;

public final class LangtonTerminationCondition
        implements
        SimulationTerminationCondition<LangtonEntity, LayeredCompositeGridModel<LangtonEntity>, LangtonStatistics> {

    @Override
    public boolean isFinished(LayeredCompositeGridModel<LangtonEntity> model, int stepCount, LangtonStatistics statistics) {
        return (statistics.getAntCells() == 0) || (statistics.getTotalCells() == statistics.getVisitedCells());
    }

}
