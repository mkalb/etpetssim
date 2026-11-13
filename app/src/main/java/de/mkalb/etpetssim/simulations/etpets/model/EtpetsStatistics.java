package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.BaseSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsChartGroup;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;

import java.util.List;

/**
 * Statistics of ET Pets with the active pet and egg cell counts and the cumulative pet deaths.
 */
public final class EtpetsStatistics extends BaseSimulationStatistics {

    public static final String KEY_ACTIVE_PET_CELLS = "activePetCells";
    public static final String KEY_EGG_CELLS = "eggCells";
    public static final String KEY_CUMULATIVE_PET_DEATH_COUNT = "cumulativePetDeathCount";

    private static final String ETPETS_OBSERVATION_CELLS_ACTIVE_PETS = "etpets.observation.cells.activepets";
    private static final String ETPETS_OBSERVATION_CELLS_EGGS = "etpets.observation.cells.eggs";
    private static final String ETPETS_OBSERVATION_CUMULATIVE_PET_DEATH_COUNT =
            "etpets.observation.cumulativepetdeathcount";
    private static final int CHART_WINDOW_SIZE = 1_000;

    private static final List<StatisticsMetric<EtpetsStatistics>> METRICS = List.of(
            new StatisticsMetric<>(KEY_ACTIVE_PET_CELLS, ETPETS_OBSERVATION_CELLS_ACTIVE_PETS,
                    EtpetsStatistics::activePetCells,
                    StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY, CHART_WINDOW_SIZE),
            new StatisticsMetric<>(KEY_EGG_CELLS, ETPETS_OBSERVATION_CELLS_EGGS,
                    EtpetsStatistics::eggCells,
                    StatisticsExtremaMode.MAX),
            new StatisticsMetric<>(KEY_CUMULATIVE_PET_DEATH_COUNT, ETPETS_OBSERVATION_CUMULATIVE_PET_DEATH_COUNT,
                    EtpetsStatistics::cumulativePetDeathCount,
                    StatisticsExtremaMode.NONE)
    );

    private int activePetCells;
    private int eggCells;
    private int cumulativePetDeathCount;

    public EtpetsStatistics(GridStructure gridStructure) {
        super(gridStructure);
    }

    public static List<StatisticsMetric<EtpetsStatistics>> metrics() {
        return METRICS;
    }

    public int activePetCells() {
        return activePetCells;
    }

    public int eggCells() {
        return eggCells;
    }

    public int cumulativePetDeathCount() {
        return cumulativePetDeathCount;
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
