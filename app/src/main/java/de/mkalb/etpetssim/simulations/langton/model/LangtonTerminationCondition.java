package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.executor.SimulationTerminationCondition;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;

/**
 * Termination condition of Langton's Ant that ends the simulation when no ant is left or all cells are visited.
 */
public final class LangtonTerminationCondition
        implements SimulationTerminationCondition<LangtonEntity, LangtonGridModel, LangtonStatistics> {

    @Override
    public boolean isFinished(LangtonGridModel model, int stepCount, LangtonStatistics statistics) {
        return (statistics.antCells() == 0) || (statistics.totalCells() == statistics.visitedCells());
    }

}
