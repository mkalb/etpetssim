package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SimulationTerminationCondition;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;

public final class SnakeTerminationCondition implements SimulationTerminationCondition<SnakeEntity,
        ReadableGridModel<SnakeEntity>, SnakeStatistics> {

    @Override
    public boolean isFinished(ReadableGridModel<SnakeEntity> model, int stepCount, SnakeStatistics statistics) {
        return statistics.getSnakeHeadCells() == 0;
    }

}
