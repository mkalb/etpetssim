package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.executor.SimulationTerminationCondition;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;

/**
 * Termination condition of Snake that ends the simulation when no snake is alive or the cumulative death count
 * reaches {@code Integer.MAX_VALUE}.
 */
public final class SnakeTerminationCondition implements SimulationTerminationCondition<SnakeEntity,
        ReadableGridModel<SnakeEntity>, SnakeStatistics> {

    @Override
    public boolean isFinished(ReadableGridModel<SnakeEntity> model, int stepCount, SnakeStatistics statistics) {
        return (statistics.livingSnakeHeadCells() <= 0)
                || (statistics.cumulativeSnakeDeathCount() == Integer.MAX_VALUE);
    }

}
