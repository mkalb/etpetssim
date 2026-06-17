package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.executor.SimulationTerminationCondition;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;

public final class EtpetsTerminationCondition
        implements SimulationTerminationCondition<EtpetsEntity, EtpetsGridModel, EtpetsStatistics> {

    @Override
    public boolean isFinished(EtpetsGridModel model, int stepCount, EtpetsStatistics statistics) {
        return (statistics.getActivePetCells() <= 0) && (statistics.getEggCells() <= 0);
    }

}

