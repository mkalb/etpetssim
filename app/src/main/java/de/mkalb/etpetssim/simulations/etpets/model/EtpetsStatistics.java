package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

public final class EtpetsStatistics extends AbstractTimedSimulationStatistics {

    private int activePetCount;
    private int eggCount;
    private int cumulativeDeadPetCount;

    public EtpetsStatistics(int totalCells) {
        super(totalCells);
        activePetCount = 0;
        eggCount = 0;
        cumulativeDeadPetCount = 0;
    }

    void update(int newStepCount,
                StepTimingStatistics newStepTimingStatistics,
                int newActivePetCount,
                int newEggCount,
                int newCumulativeDeadPetCount) {
        updateCommon(newStepCount, newStepTimingStatistics);
        activePetCount = newActivePetCount;
        eggCount = newEggCount;
        cumulativeDeadPetCount = newCumulativeDeadPetCount;
    }

    public int getActivePetCount() {
        return activePetCount;
    }

    public int getEggCount() {
        return eggCount;
    }

    public int getCumulativeDeadPetCount() {
        return cumulativeDeadPetCount;
    }

    @Override
    public String toString() {
        return "EtpetsStatistics{" +
                baseToString() +
                ", activePetCount=" + activePetCount +
                ", eggCount=" + eggCount +
                ", cumulativeDeadPetCount=" + cumulativeDeadPetCount +
                '}';
    }

}

