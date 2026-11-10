package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.executor.SimulationStepRunner;

import java.util.Random;

/**
 * Step runner of ET Pets that applies the agent, resource, and terrain phases in this order.
 */
public final class EtpetsStepRunner implements SimulationStepRunner<EtpetsStatistics> {

    private final EtpetsGridModel model;
    private final Random random;
    private final EtpetsIdSequence idSequence;

    public EtpetsStepRunner(EtpetsGridModel model,
                            Random random,
                            EtpetsIdSequence idSequence) {
        this.model = model;
        this.random = random;
        this.idSequence = idSequence;
    }

    public EtpetsGridModel model() {
        return model;
    }

    @Override
    public void performStep(int stepIndex, EtpetsStatistics statistics) {
        EtpetsAgentLogic.apply(model, random, idSequence, stepIndex, statistics);
        EtpetsResourceLogic.apply(model);
        EtpetsTerrainLogic.apply(model);
    }

}
