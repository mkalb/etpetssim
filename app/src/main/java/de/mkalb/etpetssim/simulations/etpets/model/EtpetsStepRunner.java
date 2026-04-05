package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.model.SimulationStepRunner;

import java.util.*;

public final class EtpetsStepRunner implements SimulationStepRunner<EtpetsStatistics> {

    private final EtpetsConfig config;
    private final Random random;
    private final EtpetsGridModel model;
    private final EtpetsIdSequence idSequence;

    public EtpetsStepRunner(EtpetsConfig config, Random random, EtpetsGridModel model, EtpetsIdSequence idSequence) {
        this.config = config;
        this.random = random;
        this.model = model;
        this.idSequence = idSequence;
    }

    @Override
    public void performStep(int stepIndex, EtpetsStatistics statistics) {
        EtpetsAgentLogic.apply(random, model, idSequence, stepIndex, statistics);
        EtpetsResourceLogic.apply(model);
        EtpetsTerrainLogic.apply(model);
    }

    public EtpetsGridModel model() {
        return model;
    }

}
