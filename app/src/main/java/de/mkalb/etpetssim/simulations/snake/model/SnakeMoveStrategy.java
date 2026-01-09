package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;
import org.jspecify.annotations.NonNull;

import java.util.*;

@FunctionalInterface
public interface SnakeMoveStrategy {

    /**
     * Move randomly and prioritize food.
     */
    SnakeMoveStrategy FOOD_SEEKER = new NamedSnakeMoveStrategy(
            "FoodSeeker",
            (context) -> simpleStrategyForConstants(context, 0, 2, 0));
    /**
     * Move randomly and prioritize ground.
     */
    SnakeMoveStrategy GROUND_WANDERER = new NamedSnakeMoveStrategy(
            "GroundWanderer",
            (context) -> simpleStrategyForConstants(context, 2, 0, 0));
    /**
     * Prefer to continue in the same direction and prioritize food.
     */
    SnakeMoveStrategy FOOD_WITH_MOMENTUM = new NamedSnakeMoveStrategy(
            "FoodWithMomentum",
            (context) -> simpleStrategyForConstants(context, 0, 2, 1));
    /**
     * Prefer to continue in the same direction and prioritize ground.
     */
    SnakeMoveStrategy GROUND_WITH_MOMENTUM = new NamedSnakeMoveStrategy(
            "GroundWithMomentum",
            (context) -> simpleStrategyForConstants(context, 2, 0, 1));
    /**
     * Prefer to continue in the same direction only.
     */
    SnakeMoveStrategy MOMENTUM_ONLY = new NamedSnakeMoveStrategy(
            "MomentumOnly",
            (context) -> simpleStrategyForConstants(context, 0, 0, 2));

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

    private static Optional<ScoredMove> simpleStrategyForConstants(StrategyContext context,
                                                                   int groundWeight, int foodWeight, int sameDirectionWeight) {
        CompassDirection snakeDirection = (sameDirectionWeight != 0) ? context.snakeHead().direction().orElse(null) : null;
        List<ScoredMove> scoredMoveOptions = new ArrayList<>(context.groundNeighbors().size() + context.foodNeighbors().size());
        // Score ground neighbors
        for (var neighbor : context.groundNeighbors()) {
            int score = (snakeDirection == neighbor.direction()) ? (groundWeight + sameDirectionWeight) : groundWeight;
            scoredMoveOptions.add(createScoredMove(score, neighbor, false));
        }
        // Score food neighbors
        for (var neighbor : context.foodNeighbors()) {
            int score = (snakeDirection == neighbor.direction()) ? (foodWeight + sameDirectionWeight) : foodWeight;
            scoredMoveOptions.add(createScoredMove(score, neighbor, true));
        }
        return pickRandomTopScoredMove(scoredMoveOptions, context.random());
    }

    Optional<ScoredMove> selectBestMove(StrategyContext context);

    record StrategyContext(
            SnakeHead snakeHead,
            GridCoordinate snakeHeadCoordinate,
            ReadableGridModel<SnakeEntity> model,
            List<CellNeighborWithEdgeBehavior> groundNeighbors,
            List<CellNeighborWithEdgeBehavior> foodNeighbors,
            GridStructure structure,
            SnakeConfig config,
            Random random) {}

    record ScoredMove(
            int score,
            GridCoordinate targetCoordinate,
            CompassDirection direction,
            boolean isFoodTarget) {
    }

    record NamedSnakeMoveStrategy(
            String name,
            SnakeMoveStrategy strategy) implements SnakeMoveStrategy {

        @Override
        public Optional<ScoredMove> selectBestMove(StrategyContext context) {
            return strategy.selectBestMove(context);
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }

    }

}