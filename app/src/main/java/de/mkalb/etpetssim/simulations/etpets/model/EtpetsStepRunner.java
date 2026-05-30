package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.executor.SimulationStepRunner;

import java.util.*;

public final class EtpetsStepRunner
        implements SimulationStepRunner<EtpetsStatistics> {

    private final Random random;
    private final EtpetsGridModel model;
    private final EtpetsIdSequence idSequence;

    public EtpetsStepRunner(Random random,
                            EtpetsGridModel model,
                            EtpetsIdSequence idSequence) {
        this.random = random;
        this.model = model;
        this.idSequence = idSequence;
    }

    public EtpetsGridModel model() {
        return model;
    }

    @Override
    public void performStep(int stepIndex, EtpetsStatistics statistics) {
        EtpetsAgentLogic.apply(random, model, idSequence, stepIndex, statistics);
        EtpetsResourceLogic.apply(model);
        EtpetsTerrainLogic.apply(model);
    }

}
