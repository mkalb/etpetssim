package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.executor.SimulationStepRunner;

import java.util.*;

public final class SugarStepRunner
        implements SimulationStepRunner<SugarStatistics> {

    private final SugarConfig config;
    private final Random random;
    private final SugarGridModel model;

    public SugarStepRunner(SugarConfig config,
                           Random random,
                           SugarGridModel model) {
        this.config = config;
        this.random = random;
        this.model = model;
    }

    public SugarGridModel model() {
        return model;
    }

    @Override
    public void performStep(int stepIndex, SugarStatistics statistics) {
        SugarAgentLogic.apply(config, random, model, stepIndex, statistics);
        SugarResourceLogic.apply(config, model);
    }

}
