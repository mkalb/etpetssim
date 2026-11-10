package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.AgentStepLogic;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;
import de.mkalb.etpetssim.simulations.snake.model.entity.TerrainConstant;
import de.mkalb.etpetssim.simulations.snake.model.strategy.MoveContext;
import de.mkalb.etpetssim.simulations.snake.model.strategy.MoveDecision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Agent step logic of Snake for the snake heads.
 */
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
        neighborDirectionRing = CellNeighborhoods.directionRing(config.neighborhoodMode(), structure.cellShape());
    }

    @Override
    public void performAgentStep(GridCell<SnakeEntity> agentCell,
                                 WritableGridModel<SnakeEntity> model,
                                 int stepIndex,
                                 SnakeStatistics statistics) {
        if (!(agentCell.entity() instanceof SnakeHead snakeHead)) {
            throw new IllegalArgumentException("agentCell must contain a SnakeHead, but was: " + agentCell);
        }
        var headCoordinate = agentCell.coordinate();

        if (snakeHead.isDead()) {
            removeAndRespawnDeadSnake(snakeHead, headCoordinate, model, stepIndex, statistics);
        } else {
            var context = createMoveContext(snakeHead, headCoordinate, model);

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
            case PERMADEATH -> statistics.decrementSnakeHeadCells();
            case RESPAWN -> model.findRandomDefaultCoordinate(random)
                    .ifPresentOrElse(
                            // Respawn snake head as new living snake head at free cell
                            freeCoordinate -> {
                                model.setEntity(freeCoordinate, snakeHead);
                                snakeHead.respawn(config.initialPendingGrowth(), stepIndex);
                                statistics.incrementLivingSnakeHeadCells();
                            },
                            // No free cell to respawn. Remove snake head from statistics. Similar to PERMADEATH.
                            statistics::decrementSnakeHeadCells);
        }
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void moveSnake(SnakeHead snakeHead,
                           GridCoordinate headCoordinate,
                           MoveDecision moveDecision,
                           WritableGridModel<SnakeEntity> model,
                           SnakeStatistics statistics) {
        int additionalGrowth = moveDecision.isFoodTarget() ? config.growthPerFood() : 0;
        int addedPoints = moveDecision.isFoodTarget() ?
                (config.basePointsPerFood() + (int) (snakeHead.segmentCount() * config.segmentLengthMultiplier()))
                : 0;
        // Move the snake head to the target coordinate
        Optional<GridCoordinate> tailToClear = snakeHead.move(headCoordinate, moveDecision.direction(),
                additionalGrowth, addedPoints);
        model.setEntity(moveDecision.targetCoordinate(), snakeHead);
        model.setEntity(headCoordinate, TerrainConstant.SNAKE_SEGMENT);
        // Remove tail segment if not growing
        tailToClear.ifPresent(model::setEntityToDefault);

        // Respawn food if eaten
        if (moveDecision.isFoodTarget()) {
            model.findRandomDefaultCoordinate(random)
                    .ifPresentOrElse(
                            freeCoordinate -> model.setEntity(freeCoordinate, TerrainConstant.GROWTH_FOOD),
                            statistics::decrementFoodCells);
        }
    }

    private void killSnake(SnakeHead snakeHead,
                           SnakeStatistics statistics) {
        snakeHead.die();
        statistics.decrementLivingSnakeHeadCells();
        statistics.incrementCumulativeSnakeDeathCount();
    }

    private MoveContext createMoveContext(SnakeHead snakeHead,
                                          GridCoordinate headCoordinate,
                                          ReadableGridModel<SnakeEntity> model) {
        // Find ground neighbors and food neighbors
        List<CellNeighborWithEdgeBehavior> groundNeighbors = new ArrayList<>(maxNeighbors);
        List<CellNeighborWithEdgeBehavior> foodNeighbors = new ArrayList<>(maxNeighbors);
        var cellNeighborsWithEdgeBehavior = CellNeighborhoods.cellNeighborsWithEdgeBehavior(headCoordinate,
                config.neighborhoodMode(), structure);
        for (var cellNeighborWithEdgeBehaviorList : cellNeighborsWithEdgeBehavior.values()) {
            if (cellNeighborWithEdgeBehaviorList.size() != 1) {
                throw new IllegalStateException("Unable to collect neighbors: multiple edge behaviors per coordinate, "
                        + "neighbors=" + cellNeighborsWithEdgeBehavior);
            }
            var cellNeighborWithEdgeBehavior = cellNeighborWithEdgeBehaviorList.getFirst();
            if (cellNeighborWithEdgeBehavior.edgeBehaviorAction().isWithinGrid()) {
                var neighborEntity = model.getEntity(cellNeighborWithEdgeBehavior.mappedNeighborCoordinate());
                if (neighborEntity.isGround()) {
                    groundNeighbors.add(cellNeighborWithEdgeBehavior);
                } else if (neighborEntity.isFood()) {
                    foodNeighbors.add(cellNeighborWithEdgeBehavior);
                }
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
