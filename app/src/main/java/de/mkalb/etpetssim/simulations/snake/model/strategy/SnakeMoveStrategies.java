package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeMoveStrategies {

    /**
     * Prefers to continue moving in the current direction only, ignoring cell types.
     */
    public static final SnakeMoveStrategy MOMENTUM_ONLY_STRATEGY = new NamedMoveStrategy(
            "Momentum Only",
            (context) -> scoreAndPick(context, 0, 0, 0, 2, 1, 0));

    /**
     * Combines directional momentum with a strong bias toward vertical directions (North/South).
     */
    public static final SnakeMoveStrategy VERTICAL_WITH_MOMENTUM_STRATEGY = new NamedMoveStrategy(
            "Vertical with Momentum",
            (context) -> scoreAndPick(context, 0, 0, 2, 2, 1, 0));

    /**
     * Combines directional momentum with avoidance of vertical directions (North/South).
     */
    public static final SnakeMoveStrategy AVOID_VERTICAL_WITH_MOMENTUM_STRATEGY = new NamedMoveStrategy(
            "Avoid Vertical with Momentum",
            (context) -> scoreAndPick(context, 0, 0, -2, 2, 1, 0));

    /**
     * Prioritizes moving toward ground cells.
     */
    public static final SnakeMoveStrategy PRIORITIZE_GROUND_STRATEGY = new NamedMoveStrategy(
            "Ground",
            (context) -> scoreAndPick(context, 3, 0, 0, 1, 1, 0));

    /**
     * Prioritizes ground cells while preferring straight movement and small turns.
     */
    public static final SnakeMoveStrategy PRIORITIZE_GROUND_WITH_MOMENTUM_STRATEGY = new NamedMoveStrategy(
            "Ground with Momentum",
            (context) -> scoreAndPick(context, 3, 0, 0, 2, 1, 0));

    /**
     * Prioritizes moving toward food cells. Direction is ignored.
     */
    public static final SnakeMoveStrategy PRIORITIZE_FOOD_STRATEGY = new NamedMoveStrategy(
            "Food",
            (context) -> scoreAndPick(context, 0, 3, 0, 1, 1, 0));

    /**
     * Prioritizes food cells while preferring straight movement and small turns.
     */
    public static final SnakeMoveStrategy PRIORITIZE_FOOD_WITH_MOMENTUM_STRATEGY = new NamedMoveStrategy(
            "Food with Momentum",
            (context) -> scoreAndPick(context, 0, 3, 0, 2, 1, 0));

    /**
     * Balances food targeting, directional momentum, and vertical movement.
     */
    public static final SnakeMoveStrategy BALANCED_FOOD_VERTICAL_MOMENTUM_STRATEGY = new NamedMoveStrategy(
            "Balanced Food, Vertical and Momentum",
            (context) -> scoreAndPick(context, 0, 2, 1, 2, 1, 0));

    public static final SnakeMoveStrategy FOOD_VERTICAL_CLUSTERED = new NamedMoveStrategy(
            "Food V+ Clustered",
            (context) -> scoreAndPick(context, 0, 4, 1, 1, 0, 2));

    public static final SnakeMoveStrategy FOOD_HORIZONTAL_CLUSTERED = new NamedMoveStrategy(
            "Food H+ Clustered",
            (context) -> scoreAndPick(context, 0, 4, -1, 1, 0, 2));

    public static final SnakeMoveStrategy FOOD_VERTICAL_SPREAD = new NamedMoveStrategy(
            "Food V+ Spread",
            (context) -> scoreAndPick(context, 0, 4, 1, 1, 0, -2));

    public static final SnakeMoveStrategy FOOD_HORIZONTAL_SPREAD = new NamedMoveStrategy(
            "Food H+ Spread",
            (context) -> scoreAndPick(context, 0, 4, -1, 1, 0, -2));

    /**
     * Private constructor to prevent instantiation.
     */
    private SnakeMoveStrategies() {
    }

    public static List<SnakeMoveStrategy> strategiesForConfig(SnakeConfig config) {
        List<SnakeMoveStrategy> strategies = new ArrayList<>();
        strategies.add(MOMENTUM_ONLY_STRATEGY);
        strategies.add(VERTICAL_WITH_MOMENTUM_STRATEGY);
        strategies.add(AVOID_VERTICAL_WITH_MOMENTUM_STRATEGY);
        strategies.add(PRIORITIZE_GROUND_STRATEGY);

        if (config.foodCells() > 0) {
            strategies.add(PRIORITIZE_GROUND_WITH_MOMENTUM_STRATEGY);
            strategies.add(PRIORITIZE_FOOD_STRATEGY);
            strategies.add(PRIORITIZE_FOOD_WITH_MOMENTUM_STRATEGY);
            strategies.add(BALANCED_FOOD_VERTICAL_MOMENTUM_STRATEGY);
            strategies.add(FOOD_VERTICAL_CLUSTERED);
            strategies.add(FOOD_HORIZONTAL_CLUSTERED);
            strategies.add(FOOD_VERTICAL_SPREAD);
            strategies.add(FOOD_HORIZONTAL_SPREAD);
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
                                                       int groundWeight,
                                                       int foodWeight,
                                                       int verticalWeight,
                                                       int straightWeight,
                                                       int smallTurnWeight,
                                                       int adjacentSegmentWeight) {
        // Quick checks for zero or one available move
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

        // Determine snake direction and segments for momentum and adjacent segment scoring
        CompassDirection snakeDirection = ((straightWeight != 0) || (smallTurnWeight != 0) || (verticalWeight != 0)) ? context.snakeHead().direction().orElse(null) : null;
        List<GridCoordinate> segments;
        if ((adjacentSegmentWeight != 0) && (context.snakeHead().segmentCount() > 0)) {
            segments = context.snakeHead().currentSegments();
        } else {
            segments = Collections.emptyList();
        }

        // Score all neighbor moves
        List<ScoredMove> moves = new ArrayList<>(movesCapacity);
        scoreNeighbors(context, context.groundNeighbors(), false, groundWeight,
                verticalWeight, straightWeight, smallTurnWeight, adjacentSegmentWeight,
                snakeDirection, context.neighborDirectionRing(), segments, moves);
        scoreNeighbors(context, context.foodNeighbors(), true, foodWeight,
                verticalWeight, straightWeight, smallTurnWeight, adjacentSegmentWeight,
                snakeDirection, context.neighborDirectionRing(), segments, moves);

        // Pick one of the top scored moves at random
        return pickRandomTopScoredMove(moves, context.random());
    }

    private static void scoreNeighbors(MoveContext context,
                                       List<CellNeighborWithEdgeBehavior> neighbors,
                                       boolean isFoodTarget,
                                       int baseWeight,
                                       int verticalWeight,
                                       int straightWeight,
                                       int smallTurnWeight,
                                       int adjacentSegmentWeight,
                                       @Nullable CompassDirection snakeDirection,
                                       List<CompassDirection> neighborDirectionRing,
                                       List<GridCoordinate> segments,
                                       List<ScoredMove> moves) {
        for (var neighbor : neighbors) {
            int score = baseWeight;

            if ((verticalWeight != 0) && isVertical(neighbor.direction())) {
                score += verticalWeight;
            }

            // Bias for continuing straight or making only a small turn
            if (snakeDirection != null) {
                int distance = CompassDirection.distanceOnRing(snakeDirection, neighbor.direction(), neighborDirectionRing);
                if (distance == 0) {
                    score += straightWeight;
                } else if (distance == 1) {
                    score += smallTurnWeight;
                }
                // distance >= 2 -> no momentum bonus
            }

            // Bonus for having an adjacent segment
            if ((adjacentSegmentWeight != 0) && !segments.isEmpty()
                    && hasNeighborIn(neighbor.mappedNeighborCoordinate(), segments,
                    context.config().neighborhoodMode(), context.structure())) {
                score += adjacentSegmentWeight;
            }

            // Create and add the scored move
            moves.add(createScoredMove(score, neighbor, isFoodTarget));
        }
    }

    private static boolean isVertical(CompassDirection direction) {
        return (direction == CompassDirection.N) || (direction == CompassDirection.S);
    }

    private static boolean hasNeighborIn(GridCoordinate coordinate,
                                         Collection<GridCoordinate> coordinates,
                                         NeighborhoodMode neighborhoodMode,
                                         GridStructure structure) {
        return CellNeighborhoods.cellNeighborsIgnoringEdgeBehavior(coordinate, neighborhoodMode, structure.cellShape())
                                .map(CellNeighbor::neighborCoordinate)
                                .map(nc -> CellNeighborhoods.applyEdgeBehaviorToCoordinate(nc, structure).mapped())
                                .anyMatch(coordinates::contains);
    }

    record ScoredMove(
            int score,
            GridCoordinate targetCoordinate,
            CompassDirection direction,
            boolean isFoodTarget) implements MoveDecision {
    }

}
