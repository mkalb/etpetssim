package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.AgentStepLogic;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
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
                                 SnakeStatistics context) {
        if (!(agentCell.entity() instanceof SnakeHead snakeHead)) {
            throw new IllegalArgumentException("Provided cell does not contain a SnakeHead entity");
        }
        GridCoordinate currentCoordinate = agentCell.coordinate();
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
        GridCoordinate newCoordinate = null;
        int additionalGrowth = 0;
        boolean foodConsumed = false;
        if (!foodCoordinates.isEmpty()) {
            // Choose food
            newCoordinate = foodCoordinates.getFirst(); // TODO select best food coordinate
            foodConsumed = true;
            additionalGrowth = config.growthPerFood();
        } else if (!groundCoordinates.isEmpty()) {
            // Choose ground
            newCoordinate = groundCoordinates.getFirst(); // TODO select best ground coordinate
        }
        // 3. Update model
        if (newCoordinate != null) {
            // Move the snake head to newCoordinate
            Optional<GridCoordinate> t = snakeHead.move(currentCoordinate, additionalGrowth);
            model.setEntity(newCoordinate, snakeHead);
            model.setEntity(currentCoordinate, SnakeConstantEntity.SNAKE_SEGMENT);
            // Remove tail segment if not growing
            t.ifPresent(coordinate -> model.setEntity(coordinate, SnakeConstantEntity.GROUND));
        } else {
            AppLogger.info("Snake dies: " + agentCell);
            // TODO: Possible improvement: Respawn a step later instead of immediately. The snake head needs a death state then. Movement is then not possible, and it should be rendered differently.
            model.randomDefaultCoordinate(random).ifPresent(freeCoordinate ->
                    model.setEntity(freeCoordinate, snakeHead));
            model.setEntityToDefault(currentCoordinate);
            snakeHead.currentSegments().forEach(model::setEntityToDefault);
            snakeHead.die(config.initialPendingGrowth(), stepIndex);
            AppLogger.info("Snake respawned: " + snakeHead);
        }
        if (foodConsumed) {
            model.randomDefaultCoordinate(random).ifPresent(freeCoordinate ->
                    model.setEntity(freeCoordinate, SnakeConstantEntity.GROWTH_FOOD));
        }
        // 4. Update context/statistics
    }

}
