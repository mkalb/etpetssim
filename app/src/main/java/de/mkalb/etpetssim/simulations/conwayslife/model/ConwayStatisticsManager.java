package de.mkalb.etpetssim.simulations.conwayslife.model;

import de.mkalb.etpetssim.engine.model.SimulationExecutor;

public final class ConwayStatisticsManager {

    private final ConwayStatistics statistics;

    public ConwayStatisticsManager(int cellCount) {
        statistics = new ConwayStatistics(cellCount);
    }

    public void update(SimulationExecutor<ConwayEntity> executor) {
        statistics.update(
                executor.currentStep(),
                executor.currentModel().count(cell -> cell.entity().isAlive()));
    }

    public ConwayStatistics statistics() {
        return statistics;
    }

}