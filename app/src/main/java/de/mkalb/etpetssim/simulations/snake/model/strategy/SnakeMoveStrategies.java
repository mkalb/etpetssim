package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeMoveStrategies {

    /**
     * Prefers to continue moving in the current direction only, ignoring cell types.
     */
    public static final SnakeMoveStrategy MOMENTUM_ONLY_STRATEGY = new NamedMoveStrategy(
            "Momentum Only",
            (context) -> scoreAndPick(context, 0, 0, 2, 0));

    /**
     * Combines directional momentum with a strong bias toward vertical directions (North/South).
     */
    public static final SnakeMoveStrategy VERTICAL_BIAS_MOMENTUM_STRATEGY = new NamedMoveStrategy(
            "Vertical Bias Momentum",
            (context) -> scoreAndPick(context, 0, 0, 1, 2));

    /**
     * Combines directional momentum with avoidance of vertical directions, favoring horizontal movement.
     */
    public static final SnakeMoveStrategy AVOID_VERTICAL_BIAS_STRATEGY = new NamedMoveStrategy(
            "Avoid Vertical Bias",
            (context) -> scoreAndPick(context, 0, 0, 1, -2));

    /**
     * Prioritizes moving toward ground cells, selecting randomly among equal options.
     */
    public static final SnakeMoveStrategy GROUND_PRIORITIZE_STRATEGY = new NamedMoveStrategy(
            "Ground Prioritize",
            (context) -> scoreAndPick(context, 2, 0, 0, 0));

    /**
     * Prioritizes ground cells while preferring to continue in the current direction.
     */
    public static final SnakeMoveStrategy GROUND_WITH_MOMENTUM_STRATEGY = new NamedMoveStrategy(
            "Ground with Momentum",
            (context) -> scoreAndPick(context, 2, 0, 1, 0));

    /**
     * Prioritizes moving toward food cells, selecting randomly among equal options.
     */
    public static final SnakeMoveStrategy FOOD_PRIORITIZE_STRATEGY = new NamedMoveStrategy(
            "Food Prioritize",
            (context) -> scoreAndPick(context, 0, 2, 0, 0));

    /**
     * Prioritizes food cells while preferring to continue in the current direction.
     */
    public static final SnakeMoveStrategy FOOD_WITH_MOMENTUM_STRATEGY = new NamedMoveStrategy(
            "Food with Momentum",
            (context) -> scoreAndPick(context, 0, 2, 1, 0));

    /**
     * Balances food targeting, directional momentum, and vertical bias equally.
     */
    public static final SnakeMoveStrategy FOOD_AND_MOMENTUM_BALANCED_STRATEGY = new NamedMoveStrategy(
            "Food and Momentum Balanced",
            (context) -> scoreAndPick(context, 0, 1, 1, 1));

    /**
     * Private constructor to prevent instantiation.
     */
    private SnakeMoveStrategies() {
    }

    public static List<SnakeMoveStrategy> strategiesForConfig(SnakeConfig config) {
        List<SnakeMoveStrategy> strategies = new ArrayList<>();
        strategies.add(MOMENTUM_ONLY_STRATEGY);
        strategies.add(VERTICAL_BIAS_MOMENTUM_STRATEGY);
        strategies.add(AVOID_VERTICAL_BIAS_STRATEGY);
        strategies.add(GROUND_PRIORITIZE_STRATEGY);

        if (config.foodCells() > 0) {
            strategies.add(GROUND_WITH_MOMENTUM_STRATEGY);
            strategies.add(FOOD_PRIORITIZE_STRATEGY);
            strategies.add(FOOD_WITH_MOMENTUM_STRATEGY);
            strategies.add(FOOD_AND_MOMENTUM_BALANCED_STRATEGY);
        }
        return strategies;
    }

    private static Optional<MoveDecision> pickRandomTopScoredMove(List<ScoredMove> moves, Random random) {
        // No scored moves available
        if (moves.isEmpty()) {
            return Optional.empty();
        }
        // Only one scored move available
        if (moves.size() == 1) {
            return Optional.of(moves.getFirst());
        }

        // Find all moves with the top score
        int topScore = moves.stream().mapToInt(ScoredMove::score).max().getAsInt();
        List<ScoredMove> topScoredMoves = new ArrayList<>(moves.size());
        for (var entry : moves) {
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

    private static ScoredMove createScoredMove(int score,
                                               CellNeighborWithEdgeBehavior neighbor,
                                               boolean isFoodTarget) {
        return new ScoredMove(score,
                neighbor.mappedNeighborCoordinate(),
                neighbor.direction(),
                isFoodTarget);
    }

    private static Optional<MoveDecision> scoreAndPick(MoveContext context,
                                                       int groundWeight, int foodWeight, int momentumWeight, int verticalWeight) {
        int movesCapacity = context.groundNeighbors().size() + context.foodNeighbors().size();
        if (movesCapacity == 0) {
            return Optional.empty();
        } else if (movesCapacity == 1) {
            boolean isFoodTarget = context.groundNeighbors().isEmpty();
            var onlyNeighbor = isFoodTarget
                    ? context.foodNeighbors().getFirst()
                    : context.groundNeighbors().getFirst();
            return Optional.of(createScoredMove(
                    0, // score doesn't matter with only one option
                    onlyNeighbor,
                    isFoodTarget));
        }
        CompassDirection snakeDirection = ((momentumWeight != 0) || (verticalWeight != 0)) ? context.snakeHead().direction().orElse(null) : null;
        List<ScoredMove> moves = new ArrayList<>(movesCapacity);
        scoreNeighbors(context.groundNeighbors(), false, groundWeight, momentumWeight, verticalWeight, snakeDirection, moves);
        scoreNeighbors(context.foodNeighbors(), true, foodWeight, momentumWeight, verticalWeight, snakeDirection, moves);
        return pickRandomTopScoredMove(moves, context.random());
    }

    private static void scoreNeighbors(List<CellNeighborWithEdgeBehavior> neighbors,
                                       boolean isFoodTarget, int baseWeight,
                                       int momentumWeight, int verticalWeight,
                                       @Nullable CompassDirection snakeDirection,
                                       List<ScoredMove> moves) {
        for (var neighbor : neighbors) {
            int score = baseWeight;

            if ((momentumWeight != 0) && (snakeDirection == neighbor.direction())) {
                score += momentumWeight;
            }
            if ((verticalWeight != 0) && isVertical(neighbor.direction())) {
                score += verticalWeight;
            }

            moves.add(createScoredMove(score, neighbor, isFoodTarget));
        }
    }

    private static boolean isVertical(CompassDirection direction) {
        return (direction == CompassDirection.N) || (direction == CompassDirection.S);
    }

    record ScoredMove(
            int score,
            GridCoordinate targetCoordinate,
            CompassDirection direction,
            boolean isFoodTarget) implements MoveDecision {
    }

}
