package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;
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

    private static ScoredMove createScoredMove(int score,
                                               CellNeighborWithEdgeBehavior neighbor,
                                               boolean isFoodTarget) {
        return new ScoredMove(score,
                neighbor.mappedNeighborCoordinate(),
                neighbor.direction(),
                isFoodTarget);
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
                           ScoredMove selectedMove,
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

    private Optional<ScoredMove> findAndSelectBestMove(SnakeHead snakeHead,
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
        return selectBestMove(snakeHead, model, groundNeighbors, foodNeighbors);
    }

    private Optional<ScoredMove> pickRandomTopScoredMove(List<ScoredMove> scoredMoveOptions) {
        // No scored moves available
        if (scoredMoveOptions.isEmpty()) {
            return Optional.empty();
        }
        // Only one scored move available
        if (scoredMoveOptions.size() == 1) {
            return Optional.of(scoredMoveOptions.getFirst());
        }

        // Find all moves with the top score
        int topScore = scoredMoveOptions.stream().mapToInt(ScoredMove::score).max().getAsInt();
        List<ScoredMove> topScoredMoves = new ArrayList<>(scoredMoveOptions.size());
        for (ScoredMove entry : scoredMoveOptions) {
            if (entry.score() == topScore) {
                topScoredMoves.add(entry);
            }
        }

        // Only one top scored move available
        if (topScoredMoves.size() == 1) {
            return Optional.of(topScoredMoves.getFirst());
        }

        // Randomly select one of the top scored moves
        return Optional.of(topScoredMoves.get(random.nextInt(topScoredMoves.size())));
    }

    private Optional<ScoredMove> selectBestMove(SnakeHead snakeHead,
                                                ReadableGridModel<SnakeEntity> model,
                                                List<CellNeighborWithEdgeBehavior> groundNeighbors,
                                                List<CellNeighborWithEdgeBehavior> foodNeighbors) {
        var scoredMoveOptions = switch (snakeHead.id() % 5) {
            case 0 -> scoreMoveOptions(snakeHead, groundNeighbors, foodNeighbors,
                    0, 2, 0);
            case 1 -> scoreMoveOptions(snakeHead, groundNeighbors, foodNeighbors,
                    2, 0, 0);
            case 2 -> scoreMoveOptions(snakeHead, groundNeighbors, foodNeighbors,
                    0, 2, 1);
            case 3 -> scoreMoveOptions(snakeHead, groundNeighbors, foodNeighbors,
                    2, 0, 1);
            case 4 -> scoreMoveOptions(snakeHead, groundNeighbors, foodNeighbors,
                    0, 0, 2);
            default -> throw new IllegalStateException("Unexpected value: " + snakeHead.id());
        };
        return pickRandomTopScoredMove(scoredMoveOptions);
    }

    private List<ScoredMove> scoreMoveOptions(SnakeHead snakeHead,
                                              List<CellNeighborWithEdgeBehavior> groundNeighbors,
                                              List<CellNeighborWithEdgeBehavior> foodNeighbors,
                                              int groundWeight, int foodWeight, int sameDirectionWeight) {
        CompassDirection snakeDirection = (sameDirectionWeight != 0) ? snakeHead.direction().orElse(null) : null;
        List<ScoredMove> scoredMoveOptions = new ArrayList<>(maxNeighbors);
        // Score ground neighbors
        for (var neighbor : groundNeighbors) {
            if (snakeDirection == neighbor.direction()) {
                scoredMoveOptions.add(createScoredMove(groundWeight + sameDirectionWeight, neighbor, false));
            } else {
                scoredMoveOptions.add(createScoredMove(groundWeight, neighbor, false));
            }
        }
        // Score food neighbors
        for (var neighbor : foodNeighbors) {
            if (snakeDirection == neighbor.direction()) {
                scoredMoveOptions.add(createScoredMove(foodWeight + sameDirectionWeight, neighbor, true));
            } else {
                scoredMoveOptions.add(createScoredMove(foodWeight, neighbor, true));
            }
        }
        return scoredMoveOptions;
    }

    private record ScoredMove(
            int score,
            GridCoordinate targetCoordinate,
            CompassDirection direction,
            boolean isFoodTarget) {
    }

}
