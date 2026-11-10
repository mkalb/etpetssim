package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.executor.SimulationStepRunner;

import java.util.Random;

/**
 * Step runner of Sugarscape that applies the agent phase and then the resource phase.
 */
public final class SugarStepRunner implements SimulationStepRunner<SugarStatistics> {

    private final SugarGridModel model;
    private final SugarConfig config;
    private final Random random;

    public SugarStepRunner(SugarGridModel model,
                           SugarConfig config,
                           Random random) {
        this.model = model;
        this.config = config;
        this.random = random;
    }

    public SugarGridModel model() {
        return model;
    }

    @Override
    public void performStep(int stepIndex, SugarStatistics statistics) {
        SugarAgentLogic.apply(model, config, random, stepIndex, statistics);
        SugarResourceLogic.apply(model, config);
    }

}
