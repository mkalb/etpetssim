package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.model.AgentStepLogic;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;

public final class SnakeStepLogic implements AgentStepLogic<SnakeEntity, SnakeStatistics> {

    @Override
    public void performAgentStep(GridCell<SnakeEntity> agentCell,
                                 WritableGridModel<SnakeEntity> model,
                                 int stepIndex,
                                 SnakeStatistics context) {

    }

}
