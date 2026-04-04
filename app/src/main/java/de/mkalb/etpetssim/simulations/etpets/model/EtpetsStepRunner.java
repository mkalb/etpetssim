package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.model.SimulationStepRunner;

import java.util.*;

public final class EtpetsStepRunner implements SimulationStepRunner<EtpetsStatistics> {

    private final EtpetsConfig config;
    private final EtpetsGridModel model;
    private final Random random;
    private final EtpetsIdSequence idSequence;

    public EtpetsStepRunner(EtpetsConfig config, EtpetsGridModel model, Random random, EtpetsIdSequence idSequence) {
        this.config = config;
        this.model = model;
        this.random = random;
        this.idSequence = idSequence;
    }

    @Override
    public void performStep(int stepIndex, EtpetsStatistics statistics) {
        EtpetsAgentLogic.apply(model, stepIndex, random, idSequence, statistics);
        EtpetsResourceLogic.apply(model, config, stepIndex, statistics);
        EtpetsTerrainLogic.apply(model, config, stepIndex, statistics);
    }

    public EtpetsGridModel model() {
        return model;
    }

}
