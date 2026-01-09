package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeConstantEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;

import java.util.*;

public final class SnakeStepLogic implements AgentStepLogic<SnakeEntity, SnakeStatistics> {

    private final GridStructure structure;
    private final SnakeConfig config;
    private final Random random;
    private final int maxNeighbors;

    public SnakeStepLogic(GridStructure structure, SnakeConfig config, Random random) {
        this.structure = structure;
        this.config = config;
        this.random = random;

        maxNeighbors = structure.cellShape().vertexCount();
    }

    @Override
    public void performAgentStep(GridCell<SnakeEntity> agentCell,
                                 WritableGridModel<SnakeEntity> model,
                                 int stepIndex,
                                 SnakeStatistics statistics) {
        if (!(agentCell.entity() instanceof SnakeHead snakeHead)) {
            throw new IllegalArgumentException("Provided cell does not contain a SnakeHead entity. Cell: " + agentCell);
        }
        GridCoordinate snakeHeadCoordinate = agentCell.coordinate();

        if (snakeHead.isDead()) {
            removeAndRespawnDeadSnake(snakeHead, snakeHeadCoordinate, model, stepIndex, statistics);
        } else {
            findAndSelectBestMove(snakeHead, snakeHeadCoordinate, model)
                    .ifPresentOrElse(
                            move -> moveSnake(snakeHead, snakeHeadCoordinate, move, model, statistics),
                            () -> killSnake(snakeHead, statistics)
                    );
        }
    }

    private void removeAndRespawnDeadSnake(SnakeHead snakeHead,
                                           GridCoordinate snakeHeadCoordinate,
                                           WritableGridModel<SnakeEntity> model,
                                           int stepIndex,
                                           SnakeStatistics statistics) {
        // Clear the dead snake head and all its segments from the grid model
        model.setEntityToDefault(snakeHeadCoordinate);
        snakeHead.currentSegments().forEach(model::setEntityToDefault);

        switch (config.deathMode()) {
            case PERMADEATH -> statistics.decreaseSnakeHeadCells();
            case RESPAWN -> model.randomDefaultCoordinate(random)
                                 .ifPresentOrElse(
                                         // Respawn snake head as new living snake head at free cell
                                         freeCoordinate -> {
                                             model.setEntity(freeCoordinate, snakeHead);
                                             snakeHead.respawn(config.initialPendingGrowth(), stepIndex);
                                             statistics.increaseLivingSnakeHeadCells();
                                         },
                                         // No free cell to respawn. Remove snake head from statistics. Similar to PERMADEATH.
                                         statistics::decreaseSnakeHeadCells);
        }
    }

    private void moveSnake(SnakeHead snakeHead,
                           GridCoordinate snakeHeadCoordinate,
                           SnakeMoveStrategy.ScoredMove selectedMove,
                           WritableGridModel<SnakeEntity> model,
                           SnakeStatistics statistics) {
        int additionalGrowth = selectedMove.isFoodTarget() ? config.growthPerFood() : 0;

        // Move the snake head to newCoordinate
        Optional<GridCoordinate> tailToClear = snakeHead.move(snakeHeadCoordinate, selectedMove.direction(), additionalGrowth);
        model.setEntity(selectedMove.targetCoordinate(), snakeHead);
        model.setEntity(snakeHeadCoordinate, SnakeConstantEntity.SNAKE_SEGMENT);
        // Remove tail segment if not growing
        tailToClear.ifPresent(model::setEntityToDefault);

        // Respawn food if eaten
        if (selectedMove.isFoodTarget()) {
            model.randomDefaultCoordinate(random)
                 .ifPresentOrElse(
                         freeCoordinate -> model.setEntity(freeCoordinate, SnakeConstantEntity.GROWTH_FOOD),
                         statistics::decreaseFoodCells);
        }
    }

    private void killSnake(SnakeHead snakeHead,
                           SnakeStatistics statistics) {
        snakeHead.die();
        statistics.decreaseLivingSnakeHeadCells();
        statistics.incrementDeaths();
    }

    private Optional<SnakeMoveStrategy.ScoredMove> findAndSelectBestMove(SnakeHead snakeHead,
                                                                         GridCoordinate snakeHeadCoordinate,
                                                                         ReadableGridModel<SnakeEntity> model) {
        // Find ground neighbors and food neighbors
        List<CellNeighborWithEdgeBehavior> groundNeighbors = new ArrayList<>(maxNeighbors);
        List<CellNeighborWithEdgeBehavior> foodNeighbors = new ArrayList<>(maxNeighbors);
        var cellNeighborsWithEdgeBehavior = CellNeighborhoods.cellNeighborsWithEdgeBehavior(snakeHeadCoordinate,
                config.neighborhoodMode(), structure);
        for (var cellNeighborWithEdgeBehaviorList : cellNeighborsWithEdgeBehavior.values()) {
            if (cellNeighborWithEdgeBehaviorList.size() == 1) {
                var cellNeighborWithEdgeBehavior = cellNeighborWithEdgeBehaviorList.getFirst();
                if ((cellNeighborWithEdgeBehavior.edgeBehaviorAction() == EdgeBehaviorAction.VALID)
                        || (cellNeighborWithEdgeBehavior.edgeBehaviorAction() == EdgeBehaviorAction.WRAPPED)) {
                    var neighborEntity = model.getEntity(cellNeighborWithEdgeBehavior.mappedNeighborCoordinate());
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

        // Select best move among ground and food neighbors
        return snakeHead.strategy().selectBestMove(snakeHead, snakeHeadCoordinate, model,
                groundNeighbors, foodNeighbors,
                structure, config, random);
    }

}
