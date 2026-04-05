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

    public void update(int newStepCount,
                       StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    void updateInitialCells(int activePetCountInitial,
                            int eggCountInitial,
                            int cumulativeDeadPetCountInitial) {
        activePetCount = activePetCountInitial;
        eggCount = eggCountInitial;
        cumulativeDeadPetCount = cumulativeDeadPetCountInitial;
    }

    void updateActivePetCount(int activePetCountChange) {
        activePetCount += activePetCountChange;
    }

    void updateEggCount(int eggCountChange) {
        eggCount += eggCountChange;
    }

    void updateCumulativeDeadPetCount(int cumulativeDeadPetCountChange) {
        cumulativeDeadPetCount += cumulativeDeadPetCountChange;
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

