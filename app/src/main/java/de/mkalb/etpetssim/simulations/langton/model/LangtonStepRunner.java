package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.CompositeGridModel;
import de.mkalb.etpetssim.engine.model.SimulationStepRunner;

public record LangtonStepRunner(CompositeGridModel<LangtonEntity> compositeGridModel)
        implements SimulationStepRunner<LangtonStatistics> {

    @Override
    public void performStep(int stepIndex, LangtonStatistics context) {
        // TODO implement performStep
    }

}
