package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeConstantEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;
import de.mkalb.etpetssim.simulations.snake.model.strategy.MoveContext;
import de.mkalb.etpetssim.simulations.snake.model.strategy.MoveDecision;

import java.util.*;

public final class SnakeStepLogic implements AgentStepLogic<SnakeEntity, SnakeStatistics> {

    private final GridStructure structure;
    private final SnakeConfig config;
    private final Random random;
    private final int maxNeighbors;
    private final List<CompassDirection> neighborDirectionRing;

    public SnakeStepLogic(GridStructure structure, SnakeConfig config, Random random) {
        this.structure = structure;
        this.config = config;
        this.random = random;

        maxNeighbors = structure.cellShape().vertexCount();
        if (structure.cellShape() == CellShape.HEXAGON) {
            neighborDirectionRing = CellNeighborhoods.HEXAGON_DIRECTION_RING;
        } else if ((structure.cellShape() == CellShape.SQUARE)
                && (config.neighborhoodMode() == NeighborhoodMode.EDGES_ONLY)) {
            neighborDirectionRing = CellNeighborhoods.SQUARE_EDGES_DIRECTION_RING;
        } else {
            throw new IllegalArgumentException("Unsupported combination of cell shape and neighborhood mode: "
                    + structure.cellShape() + ", " + config.neighborhoodMode());
        }
    }

    @Override
    public void performAgentStep(GridCell<SnakeEntity> agentCell,
                                 WritableGridModel<SnakeEntity> model,
                                 int stepIndex,
                                 SnakeStatistics statistics) {
        if (!(agentCell.entity() instanceof SnakeHead snakeHead)) {
            throw new IllegalArgumentException("Provided cell does not contain a SnakeHead entity. Cell: " + agentCell);
        }
        var headCoordinate = agentCell.coordinate();

        if (snakeHead.isDead()) {
            removeAndRespawnDeadSnake(snakeHead, headCoordinate, model, stepIndex, statistics);
        } else {
            var context = buildStrategyContext(snakeHead, headCoordinate, model);

            // Decide move by strategy and act accordingly (move or die)
            snakeHead.strategy().decideMove(context).ifPresentOrElse(
                    move -> moveSnake(snakeHead, headCoordinate, move, model, statistics),
                    () -> killSnake(snakeHead, statistics)
            );
        }
    }

    private void removeAndRespawnDeadSnake(SnakeHead snakeHead,
                                           GridCoordinate headCoordinate,
                                           WritableGridModel<SnakeEntity> model,
                                           int stepIndex,
                                           SnakeStatistics statistics) {
        // Clear the dead snake head and all its segments from the grid model
        model.setEntityToDefault(headCoordinate);
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
                           GridCoordinate headCoordinate,
                           MoveDecision moveDecision,
                           WritableGridModel<SnakeEntity> model,
                           SnakeStatistics statistics) {
        int additionalGrowth = moveDecision.isFoodTarget() ? config.growthPerFood() : 0;
        int addedPoints = moveDecision.isFoodTarget() ?
                (config.basePointsPerFood() + (int) (snakeHead.segmentCount() * config.segmentLengthMultiplier()))
                : 0;
        // Move the snake head to newCoordinate
        Optional<GridCoordinate> tailToClear = snakeHead.move(headCoordinate, moveDecision.direction(), additionalGrowth, addedPoints);
        model.setEntity(moveDecision.targetCoordinate(), snakeHead);
        model.setEntity(headCoordinate, SnakeConstantEntity.SNAKE_SEGMENT);
        // Remove tail segment if not growing
        tailToClear.ifPresent(model::setEntityToDefault);

        // Respawn food if eaten
        if (moveDecision.isFoodTarget()) {
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

    private MoveContext buildStrategyContext(SnakeHead snakeHead,
                                             GridCoordinate headCoordinate,
                                             ReadableGridModel<SnakeEntity> model) {
        // Find ground neighbors and food neighbors
        List<CellNeighborWithEdgeBehavior> groundNeighbors = new ArrayList<>(maxNeighbors);
        List<CellNeighborWithEdgeBehavior> foodNeighbors = new ArrayList<>(maxNeighbors);
        var cellNeighborsWithEdgeBehavior = CellNeighborhoods.cellNeighborsWithEdgeBehavior(headCoordinate,
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
        return new MoveContext(
                snakeHead,
                headCoordinate,
                model,
                groundNeighbors,
                foodNeighbors,
                structure,
                neighborDirectionRing,
                config,
                random
        );
    }

}
