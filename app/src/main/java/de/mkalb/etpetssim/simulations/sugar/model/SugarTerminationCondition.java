package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.executor.SimulationTerminationCondition;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;

/**
 * Termination condition for the Sugarscape simulation.
 * <p>
 * The simulation ends when no agent cells remain. Under normal operation this condition
 * is never triggered, because {@link SugarAgentLogic} immediately spawns a replacement
 * for every agent that dies. It acts as a safety net when the simulation is started
 * with zero agents or when agent spawning repeatedly fails on a fully occupied grid.
 */
public final class SugarTerminationCondition
        implements
        SimulationTerminationCondition<SugarEntity, SugarGridModel, SugarStatistics> {

    @Override
    public boolean isFinished(SugarGridModel model, int stepCount, SugarStatistics statistics) {
        return statistics.getAgentCells() == 0;
    }

}
