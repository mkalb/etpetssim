package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.*;

import java.util.*;

/**
 * Holds runtime statistics for a running simulation.
 */
public final class EtpetsStatistics extends BaseTimedSimulationStatistics {

    private static final String ETPETS_OBSERVATION_ACTIVE_PET_CELLS = "etpets.observation.cells.activepets";
    private static final String ETPETS_OBSERVATION_EGG_CELLS = "etpets.observation.cells.eggs";
    private static final String ETPETS_OBSERVATION_CUMULATIVE_PET_DEATH_COUNT = "etpets.observation.cumulativepetdeathcount";

    private int activePetCells;
    private int eggCells;

    private int cumulativePetDeathCount;

    public EtpetsStatistics(GridStructure gridStructure) {
        super(gridStructure);
        activePetCells = 0;
        eggCells = 0;
        cumulativePetDeathCount = 0;
    }

    public static List<StatisticMetric<EtpetsStatistics>> metrics() {
        return List.of(
                new StatisticMetric<>("activePetCells", ETPETS_OBSERVATION_ACTIVE_PET_CELLS, EtpetsStatistics::getActivePetCells,
                        StatisticExtremaMode.MIN_AND_MAX),
                new StatisticMetric<>("eggCells", ETPETS_OBSERVATION_EGG_CELLS, EtpetsStatistics::getEggCells,
                        StatisticExtremaMode.MIN_AND_MAX),
                new StatisticMetric<>("cumulativePetDeathCount", ETPETS_OBSERVATION_CUMULATIVE_PET_DEATH_COUNT,
                        EtpetsStatistics::getCumulativePetDeathCount, StatisticExtremaMode.NONE)
        );
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
