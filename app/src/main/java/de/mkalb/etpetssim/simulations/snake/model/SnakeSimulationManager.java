package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeConstantEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;

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
        Comparator<GridCell<SnakeEntity>> agentOrderingStrategy = Comparator.comparingInt(cell -> {
            SnakeEntity e = cell.entity();
            if (e instanceof SnakeHead head) {
                return head.id();
            }
            return Integer.MAX_VALUE;
        });
        var agentStepLogic = new SnakeStepLogic(structure, config, random);
        var runner = new AsynchronousStepRunner<>(model, SnakeEntity::isAgent, agentOrderingStrategy, agentStepLogic);
        var terminationCondition = new SnakeTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model, random);

        updateInitialStatistics(model);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void initializeGrid(SnakeConfig config, WritableGridModel<SnakeEntity> model, Random random) {
        // // initialize WALL
        // GridInitializer<SnakeEntity> wallInit = GridInitializers.placeRandomPercent(
        //         () -> SnakeConstantEntity.WALL,
        //         SnakeEntity::isGround,
        //         0.05d,
        //         random);
        // wallInit.initialize(model);

        // initialize SNAKE_HEAD
        List<SnakeEntity> snakeHeads = new ArrayList<>();
        for (int i = 0; i < config.initialSnakes(); i++) {
            snakeHeads.add(new SnakeHead(i, config.initialPendingGrowth(), -1));
        }
        GridInitializer<SnakeEntity> snakeInit = GridInitializers.placeAllAtRandomPositions(
                snakeHeads,
                SnakeEntity::isGround,
                random);
        snakeInit.initialize(model);

        // initialize GROWTH_FOOD
        int freeGroundCells = (int) model.countEntities(SnakeEntity::isGround);
        int initialFoodCells = Math.min(config.initialFoodCells(), freeGroundCells);
        GridInitializer<SnakeEntity> foodInit = GridInitializers.placeShuffledCounted(
                initialFoodCells,
                () -> SnakeConstantEntity.GROWTH_FOOD,
                SnakeEntity::isGround,
                random);
        foodInit.initialize(model);
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

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void updateInitialStatistics(ReadableGridModel<SnakeEntity> model) {
        int initialSnakes = (int) model.countEntities(e -> Objects.equals(e.descriptorId(), SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD));
        int initialFoodCells = (int) model.countEntities(e -> Objects.equals(e.descriptorId(), SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD));
        statistics.updateInitialCells(
                initialSnakes,
                initialFoodCells);
    }

    @Override
    protected TimedSimulationExecutor<SnakeEntity, WritableGridModel<SnakeEntity>> executor() {
        return executor;
    }

}
