package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeConstantEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;

import java.util.*;

public final class SnakeSimulationManager
        extends AbstractTimedSimulationManager<SnakeEntity, WritableGridModel<SnakeEntity>, SnakeConfig,
        SnakeStatistics> {

    private final GridStructure structure;
    private final SnakeStatistics statistics;
    private final TimedSimulationExecutor<SnakeEntity, WritableGridModel<SnakeEntity>> executor;

    public SnakeSimulationManager(SnakeConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new SnakeStatistics(structure.cellCount());
        var random = new Random(config.seed());
        var model = new SparseGridModel<SnakeEntity>(structure, SnakeConstantEntity.GROUND);

        // Executor with runner and terminationCondition
        Comparator<GridCell<SnakeEntity>> agentOrderingStrategy = null;
        AgentStepLogic<SnakeEntity, SnakeStatistics> agentStepLogic = null;
        var runner = new AsynchronousStepRunner<>(model, SnakeEntity::isAgent, agentOrderingStrategy, agentStepLogic);
        var terminationCondition = new SnakeTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public SnakeStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @Override
    protected TimedSimulationExecutor<SnakeEntity, WritableGridModel<SnakeEntity>> executor() {
        return executor;
    }

}
