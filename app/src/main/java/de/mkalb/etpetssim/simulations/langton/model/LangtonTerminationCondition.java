package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.CompositeGridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;

public final class LangtonTerminationCondition
        implements SimulationTerminationCondition<LangtonEntity, CompositeGridModel<LangtonEntity>, LangtonStatistics> {

    @Override
    public boolean isFinished(CompositeGridModel<LangtonEntity> model, int stepCount, LangtonStatistics statistics) {
        return (statistics.getAntCells() == 0) || (statistics.getTotalCells() == statistics.getVisitedCells());
    }

}
