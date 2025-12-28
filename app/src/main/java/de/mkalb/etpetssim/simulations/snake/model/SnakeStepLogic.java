package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;
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
        List<CellNeighborWithEdgeBehavior> groundNeighbors = new ArrayList<>();
        List<CellNeighborWithEdgeBehavior> foodNeighbors = new ArrayList<>();
        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> cellNeighborsWithEdgeBehavior = CellNeighborhoods.cellNeighborsWithEdgeBehavior(currentCoordinate, config.neighborhoodMode(), structure);
        for (List<CellNeighborWithEdgeBehavior> cellNeighborWithEdgeBehaviorList : cellNeighborsWithEdgeBehavior.values()) {
            if (cellNeighborWithEdgeBehaviorList.size() == 1) {
                CellNeighborWithEdgeBehavior cellNeighborWithEdgeBehavior = cellNeighborWithEdgeBehaviorList.getFirst();
                if ((cellNeighborWithEdgeBehavior.edgeBehaviorAction() == EdgeBehaviorAction.VALID) || (cellNeighborWithEdgeBehavior.edgeBehaviorAction() == EdgeBehaviorAction.WRAPPED)) {
                    SnakeEntity neighborEntity = model.getEntity(cellNeighborWithEdgeBehavior.mappedNeighborCoordinate());
                    if (neighborEntity.isGround()) {
                        groundNeighbors.add(cellNeighborWithEdgeBehavior);
                    } else if (Objects.equals(neighborEntity.descriptorId(), SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD)) {
                        foodNeighbors.add(cellNeighborWithEdgeBehavior);
                    }
                }
            } else {
                throw new IllegalStateException("Multiple edge behaviors for the same neighbor coordinate. " + cellNeighborsWithEdgeBehavior);
            }
        }
        // 2. Choose move
        Optional<MoveDecision> optionalMoveDecision = chooseMoveCoordinate(snakeHead, model,
                groundNeighbors, foodNeighbors);
        // 3. Update model
        if (optionalMoveDecision.isPresent()) {
            MoveDecision moveDecision = optionalMoveDecision.get();
            int additionalGrowth = 0;
            boolean foodConsumed = false;

            if (moveDecision.foodConsumed()) {
                foodConsumed = true;
                additionalGrowth = config.growthPerFood();
            }
            // Move the snake head to newCoordinate
            Optional<GridCoordinate> t = snakeHead.move(currentCoordinate, moveDecision.direction(), additionalGrowth);
            model.setEntity(moveDecision.coordinate(), snakeHead);
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

    private Optional<MoveDecision> chooseMoveCoordinate(SnakeHead snakeHead,
                                                        ReadableGridModel<SnakeEntity> model,
                                                        List<CellNeighborWithEdgeBehavior> groundNeighbors,
                                                        List<CellNeighborWithEdgeBehavior> foodNeighbors) {
        return chooseMoveCoordinateRandom(groundNeighbors, foodNeighbors);
    }

    private Optional<MoveDecision> chooseMoveCoordinateRandom(List<CellNeighborWithEdgeBehavior> groundNeighbors,
                                                              List<CellNeighborWithEdgeBehavior> foodNeighbors) {

        if (!foodNeighbors.isEmpty()) {
            var neighbor = foodNeighbors.get(random.nextInt(foodNeighbors.size()));
            return Optional.of(new MoveDecision(
                    neighbor.mappedNeighborCoordinate(),
                    neighbor.direction(),
                    true));
        } else if (!groundNeighbors.isEmpty()) {
            var neighbor = groundNeighbors.get(random.nextInt(groundNeighbors.size()));
            return Optional.of(new MoveDecision(
                    neighbor.mappedNeighborCoordinate(),
                    neighbor.direction(),
                    false
            ));
        }
        return Optional.empty();
    }

    private record MoveDecision(GridCoordinate coordinate, CompassDirection direction, boolean foodConsumed) {
    }

}
