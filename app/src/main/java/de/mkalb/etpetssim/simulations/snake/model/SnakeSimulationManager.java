package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeConstantEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategy;

import java.util.*;

public final class SnakeSimulationManager
        extends AbstractTimedSimulationManager<SnakeEntity, WritableGridModel<SnakeEntity>, SnakeConfig,
        SnakeStatistics> {

    private static final Comparator<GridCell<SnakeEntity>> AGENT_ORDERING_STRATEGY =
            Comparator.comparingInt(cell -> {
                SnakeEntity e = cell.entity();
                if (e instanceof SnakeHead head) {
                    return head.id();
                }
                return Integer.MAX_VALUE;
            });

    private static final int WALL_MAX_WIDTH_SPACE = 2;
    private static final int WALL_MAX_HEIGHT_SPACE = 4;

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
        var agentStepLogic = new SnakeStepLogic(structure, config, random);
        var runner = new AsynchronousStepRunner<>(model, SnakeEntity::isAgent, AGENT_ORDERING_STRATEGY, agentStepLogic);
        var terminationCondition = new SnakeTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model, random);

        updateInitialStatistics(model);
    }

    private void initializeGrid(SnakeConfig config, WritableGridModel<SnakeEntity> model, Random random) {
        wallInitializer(config, random).initialize(model);
        snakeInitializer(config, random).initialize(model);
        foodInitializer(config, model, random).initialize(model);
    }

    private GridInitializer<SnakeEntity> wallInitializer(SnakeConfig config, Random random) {
        int width = structure.size().width();
        int height = structure.size().height();
        int wallCount = config.verticalWalls();

        if ((wallCount > 0)
                && (width >= ((wallCount * (1 + WALL_MAX_WIDTH_SPACE)) + WALL_MAX_WIDTH_SPACE))
                && (height > WALL_MAX_HEIGHT_SPACE)) {
            // Integer division (floor), ensures that walls have equal spacing and fit within the grid
            int distanceX = width / wallCount;
            // Center walls within the grid. Leave additional space on the left and right.
            int adjustment = (-1 - ((distanceX - 1) / 2)) + ((width % wallCount) / 2);

            // Calculate wall x-positions
            Set<Integer> wallXPositions = new TreeSet<>();
            for (int i = 1; i <= wallCount; i++) {
                int x = (i * distanceX) + adjustment;
                wallXPositions.add(x);
            }

            // Calculate wall y-positions and create wall cells
            List<GridCell<SnakeEntity>> cells = new ArrayList<>();
            for (int x : wallXPositions) {
                int yStart = random.nextInt(height);
                int maxLength = Math.min(height - WALL_MAX_HEIGHT_SPACE, height - yStart);
                int length = random.nextInt(maxLength);
                int yEnd = yStart + length;

                for (int y = yStart; y <= yEnd; y++) {
                    cells.add(new GridCell<>(new GridCoordinate(x, y), SnakeConstantEntity.WALL));
                }
            }
            return GridInitializers.fromList(cells);
        }
        return GridInitializers.identity();
    }

    private GridInitializer<SnakeEntity> snakeInitializer(SnakeConfig config, Random random) {
        if (config.snakes() > 0) {
            SnakeMoveStrategy[] strategies = {
                    SnakeMoveStrategies.FOOD_SEEKER,
                    SnakeMoveStrategies.GROUND_WANDERER,
                    SnakeMoveStrategies.FOOD_WITH_MOMENTUM,
                    SnakeMoveStrategies.GROUND_WITH_MOMENTUM,
                    SnakeMoveStrategies.MOMENTUM_ONLY
            };

            List<SnakeEntity> snakeHeads = new ArrayList<>(config.snakes());
            for (int i = 0; i < config.snakes(); i++) {
                SnakeMoveStrategy strategy = strategies[i % strategies.length];
                snakeHeads.add(new SnakeHead(i, strategy, config.initialPendingGrowth(), -1));
            }
            return GridInitializers.placeAllAtRandomPositions(
                    snakeHeads,
                    SnakeEntity::isGround,
                    random);
        }
        return GridInitializers.identity();
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private GridInitializer<SnakeEntity> foodInitializer(SnakeConfig config, WritableGridModel<SnakeEntity> model, Random random) {
        if (config.foodCells() > 0) {
            int freeGroundCells = (int) model.countEntities(SnakeEntity::isGround);
            int foodCells = Math.min(config.foodCells(), freeGroundCells);
            return GridInitializers.placeShuffledCounted(
                    foodCells,
                    () -> SnakeConstantEntity.GROWTH_FOOD,
                    SnakeEntity::isGround,
                    random);
        }
        return GridInitializers.identity();
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
        int snakes = (int) model.countEntities(e -> Objects.equals(e.descriptorId(), SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD));
        int foodCells = (int) model.countEntities(e -> Objects.equals(e.descriptorId(), SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD));
        statistics.updateInitialCells(
                snakes,
                foodCells);
    }

    @Override
    protected TimedSimulationExecutor<SnakeEntity, WritableGridModel<SnakeEntity>> executor() {
        return executor;
    }

}
