package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseTimedSimulationStatistics;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class EtpetsStatistics extends BaseTimedSimulationStatistics {

    private int activePetCells;
    private int eggCells;

    private int cumulativePetDeathCount;

    public EtpetsStatistics(GridStructure gridStructure) {
        super(gridStructure);
        activePetCells = 0;
        eggCells = 0;
        cumulativePetDeathCount = 0;
    }

    void initializeStartupCellCounts(int activePetCountInitial,
                                     int eggCountInitial) {
        activePetCells = activePetCountInitial;
        eggCells = eggCountInitial;
    }

    void adjustCellCounts(int activePetCellsDelta,
                          int eggCellsDelta,
                          int cumulativePetDeathCountDelta) {
        activePetCells += activePetCellsDelta;
        eggCells += eggCellsDelta;
        cumulativePetDeathCount += cumulativePetDeathCountDelta;
    }

    public int getActivePetCells() {
        return activePetCells;
    }

    public int getEggCells() {
        return eggCells;
    }

    public int getCumulativePetDeathCount() {
        return cumulativePetDeathCount;
    }

    @Override
    public String toString() {
        return "EtpetsStatistics{" +
                baseToString() +
                ", activePetCells=" + activePetCells +
                ", eggCells=" + eggCells +
                ", cumulativePetDeathCount=" + cumulativePetDeathCount +
                '}';
    }

}
