package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;

import java.util.*;

public final class SnakeMoveStrategies {

    /**
     * Move randomly and prioritize food.
     * Name: FoodSeeker
     */
    public static final SnakeMoveStrategy FOOD_SEEKER = new NamedMoveStrategy(
            "FoodSeeker",
            (context) -> scoreAndPick(context, 0, 2, 0));
    /**
     * Move randomly and prioritize ground.
     * Name: GroundWanderer
     */
    public static final SnakeMoveStrategy GROUND_WANDERER = new NamedMoveStrategy(
            "GroundWanderer",
            (context) -> scoreAndPick(context, 2, 0, 0));
    /**
     * Prefer to continue in the same direction and prioritize food.
     * Name: FoodWithMomentum
     */
    public static final SnakeMoveStrategy FOOD_WITH_MOMENTUM = new NamedMoveStrategy(
            "FoodWithMomentum",
            (context) -> scoreAndPick(context, 0, 2, 1));
    /**
     * Prefer to continue in the same direction and prioritize ground.
     * Name: GroundWithMomentum
     */
    public static final SnakeMoveStrategy GROUND_WITH_MOMENTUM = new NamedMoveStrategy(
            "GroundWithMomentum",
            (context) -> scoreAndPick(context, 2, 0, 1));
    /**
     * Prefer to continue in the same direction only.
     * Name: MomentumOnly
     */
    public static final SnakeMoveStrategy MOMENTUM_ONLY = new NamedMoveStrategy(
            "MomentumOnly",
            (context) -> scoreAndPick(context, 0, 0, 2));

    /**
     * Private constructor to prevent instantiation.
     */
    private SnakeMoveStrategies() {
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
                                                       int groundWeight, int foodWeight, int momentumWeight) {
        CompassDirection snakeDirection = (momentumWeight != 0) ? context.snakeHead().direction().orElse(null) : null;
        List<ScoredMove> moves = new ArrayList<>(context.groundNeighbors().size() + context.foodNeighbors().size());
        // Score ground neighbors
        for (var neighbor : context.groundNeighbors()) {
            int score = (snakeDirection == neighbor.direction()) ? (groundWeight + momentumWeight) : groundWeight;
            moves.add(createScoredMove(score, neighbor, false));
        }
        // Score food neighbors
        for (var neighbor : context.foodNeighbors()) {
            int score = (snakeDirection == neighbor.direction()) ? (foodWeight + momentumWeight) : foodWeight;
            moves.add(createScoredMove(score, neighbor, true));
        }
        return pickRandomTopScoredMove(moves, context.random());
    }

    record ScoredMove(
            int score,
            GridCoordinate targetCoordinate,
            CompassDirection direction,
            boolean isFoodTarget) implements MoveDecision {
    }

}
