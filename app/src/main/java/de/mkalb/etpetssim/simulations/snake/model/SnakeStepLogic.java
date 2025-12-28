package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeConstantEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;

import java.util.*;

@SuppressWarnings("ClassCanBeRecord")
public final class SnakeStepLogic implements AgentStepLogic<SnakeEntity, SnakeStatistics> {

    private final GridStructure structure;
    private final SnakeConfig config;
    private final Random random;

    public SnakeStepLogic(GridStructure structure, SnakeConfig config, Random random) {
        this.structure = structure;
        this.config = config;
        this.random = random;
    }

    @Override
    public void performAgentStep(GridCell<SnakeEntity> agentCell,
                                 WritableGridModel<SnakeEntity> model,
                                 int stepIndex,
                                 SnakeStatistics statistics) {
        if (!(agentCell.entity() instanceof SnakeHead snakeHead)) {
            throw new IllegalArgumentException("Provided cell does not contain a SnakeHead entity");
        }
        GridCoordinate currentCoordinate = agentCell.coordinate();

        if (snakeHead.isDead()) {
            if (config.deathMode() == SnakeDeathMode.PERMADEATH) {
                model.setEntityToDefault(currentCoordinate);
                snakeHead.currentSegments().forEach(model::setEntityToDefault);
                statistics.decreaseSnakeHeadCells();
                return;
            }
            Optional<GridCoordinate> freeCoordinate = model.randomDefaultCoordinate(random);

            model.setEntityToDefault(currentCoordinate);
            snakeHead.currentSegments().forEach(model::setEntityToDefault);

            if (freeCoordinate.isPresent()) {
                model.setEntity(freeCoordinate.get(), snakeHead);
                snakeHead.respawn(config.initialPendingGrowth(), stepIndex);
            } else {
                statistics.decreaseSnakeHeadCells();
            }

            return;
        }
        // 1. Find possible moves (ground or food)
        List<GridCoordinate> groundCoordinates = new ArrayList<>();
        List<GridCoordinate> foodCoordinates = new ArrayList<>();
        Collection<EdgeBehaviorResult> edgeBehaviorResults = CellNeighborhoods.neighborEdgeResults(currentCoordinate, config.neighborhoodMode(), structure);
        for (var result : edgeBehaviorResults) {
            if ((result.action() == EdgeBehaviorAction.VALID) || (result.action() == EdgeBehaviorAction.WRAPPED)) {
                SnakeEntity neighborEntity = model.getEntity(result.mapped());
                if (neighborEntity.isGround()) {
                    groundCoordinates.add(result.mapped());
                } else if (Objects.equals(neighborEntity.descriptorId(), SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD)) {
                    foodCoordinates.add(result.mapped());
                }
            }
        }
        // 2. Choose move
        Optional<GridCoordinate> newCoordinate = chooseMoveCoordinate(snakeHead, model,
                groundCoordinates, foodCoordinates);
        // 3. Update model
        if (newCoordinate.isPresent()) {
            int additionalGrowth = 0;
            boolean foodConsumed = false;

            if (foodCoordinates.contains(newCoordinate.get())) {
                foodConsumed = true;
                additionalGrowth = config.growthPerFood();
            }
            // Move the snake head to newCoordinate
            Optional<GridCoordinate> t = snakeHead.move(currentCoordinate, additionalGrowth);
            model.setEntity(newCoordinate.get(), snakeHead);
            model.setEntity(currentCoordinate, SnakeConstantEntity.SNAKE_SEGMENT);
            // Remove tail segment if not growing
            t.ifPresent(model::setEntityToDefault);

            if (foodConsumed) {
                Optional<GridCoordinate> freeCoordinate = model.randomDefaultCoordinate(random);
                if (freeCoordinate.isPresent()) {
                    model.setEntity(freeCoordinate.get(), SnakeConstantEntity.GROWTH_FOOD);
                } else {
                    statistics.decreaseFoodCells();
                }
            }
        } else {
            snakeHead.die();
            statistics.incrementDeaths();
        }

        // 4. Update context/statistics
    }

    private Optional<GridCoordinate> chooseMoveCoordinate(SnakeHead snakeHead,
                                                          ReadableGridModel<SnakeEntity> model,
                                                          List<GridCoordinate> groundCoordinates,
                                                          List<GridCoordinate> foodCoordinates) {

        if (!foodCoordinates.isEmpty()) {
            return Optional.of(foodCoordinates.get(random.nextInt(foodCoordinates.size())));
        } else if (!groundCoordinates.isEmpty()) {
            // Choose ground
            return Optional.of(groundCoordinates.get(random.nextInt(groundCoordinates.size())));
        }
        return Optional.empty();
    }

}
