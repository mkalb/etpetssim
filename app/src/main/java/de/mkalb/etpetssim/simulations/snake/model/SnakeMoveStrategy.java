package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;

import java.util.*;

@FunctionalInterface
public interface SnakeMoveStrategy {

    SnakeMoveStrategy FOOD_SEEKER = (snakeHead, _, _,
                                     groundNeighbors, foodNeighbors,
                                     _, _, random) ->
            pickRandomTopScoredMove(scoreMoveOptions(snakeHead, groundNeighbors, foodNeighbors,
                    0, 2, 0), random);
    SnakeMoveStrategy GROUND_WANDERER = (snakeHead, _, _,
                                         groundNeighbors, foodNeighbors,
                                         _, _, random) ->
            pickRandomTopScoredMove(scoreMoveOptions(snakeHead,
                    groundNeighbors, foodNeighbors,
                    2, 0, 0), random);
    SnakeMoveStrategy FOOD_WITH_MOMENTUM = (snakeHead, _, _,
                                            groundNeighbors, foodNeighbors,
                                            _, _, random) ->
            pickRandomTopScoredMove(scoreMoveOptions(snakeHead,
                    groundNeighbors, foodNeighbors,
                    0, 2, 1), random);
    SnakeMoveStrategy GROUND_WITH_MOMENTUM = (snakeHead, _, _,
                                              groundNeighbors, foodNeighbors,
                                              _, _, random) ->
            pickRandomTopScoredMove(scoreMoveOptions(snakeHead,
                    groundNeighbors, foodNeighbors,
                    2, 0, 1), random);
    SnakeMoveStrategy MOMENTUM_ONLY = (snakeHead, _, _,
                                       groundNeighbors, foodNeighbors,
                                       _, _, random) ->
            pickRandomTopScoredMove(scoreMoveOptions(snakeHead,
                    groundNeighbors, foodNeighbors,
                    0, 0, 2), random);

    static Optional<ScoredMove> pickRandomTopScoredMove(List<ScoredMove> scoredMoveOptions, Random random) {
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
        for (var entry : scoredMoveOptions) {
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

    static ScoredMove createScoredMove(int score,
                                       CellNeighborWithEdgeBehavior neighbor,
                                       boolean isFoodTarget) {
        return new ScoredMove(score,
                neighbor.mappedNeighborCoordinate(),
                neighbor.direction(),
                isFoodTarget);
    }

    static List<ScoredMove> scoreMoveOptions(SnakeHead snakeHead,
                                             List<CellNeighborWithEdgeBehavior> groundNeighbors,
                                             List<CellNeighborWithEdgeBehavior> foodNeighbors,
                                             int groundWeight, int foodWeight, int sameDirectionWeight) {
        CompassDirection snakeDirection = (sameDirectionWeight != 0) ? snakeHead.direction().orElse(null) : null;
        List<ScoredMove> scoredMoveOptions = new ArrayList<>(groundNeighbors.size() + foodNeighbors.size());
        // Score ground neighbors
        for (var neighbor : groundNeighbors) {
            int score = (snakeDirection == neighbor.direction()) ? (groundWeight + sameDirectionWeight) : groundWeight;
            scoredMoveOptions.add(createScoredMove(score, neighbor, false));
        }
        // Score food neighbors
        for (var neighbor : foodNeighbors) {
            int score = (snakeDirection == neighbor.direction()) ? (foodWeight + sameDirectionWeight) : foodWeight;
            scoredMoveOptions.add(createScoredMove(score, neighbor, true));
        }
        return scoredMoveOptions;
    }

    Optional<ScoredMove> selectBestMove(SnakeHead snakeHead,
                                        GridCoordinate snakeHeadCoordinate,
                                        ReadableGridModel<SnakeEntity> model,
                                        List<CellNeighborWithEdgeBehavior> groundNeighbors,
                                        List<CellNeighborWithEdgeBehavior> foodNeighbors,
                                        GridStructure structure,
                                        SnakeConfig config,
                                        Random random
    );

    record ScoredMove(
            int score,
            GridCoordinate targetCoordinate,
            CompassDirection direction,
            boolean isFoodTarget) {
    }

}