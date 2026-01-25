package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.neighborhood.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeMoveStrategies {

    public static final SnakeMoveStrategy MOMENTUM = new NamedMoveStrategy(
            "M",
            (context) -> scoreAndPick(context, 0, 1, 0, 3, 1, 0));
    public static final SnakeMoveStrategy VERTICAL_MOMENTUM = new NamedMoveStrategy(
            "V M",
            (context) -> scoreAndPick(context, 0, 1, 2, 3, 1, 0));
    public static final SnakeMoveStrategy HORIZONTAL_MOMENTUM = new NamedMoveStrategy(
            "H M",
            (context) -> scoreAndPick(context, 0, 1, -2, 3, 1, 0));
    public static final SnakeMoveStrategy GROUND = new NamedMoveStrategy(
            "G",
            (context) -> scoreAndPick(context, 3, 0, 0, 1, 1, 0));
    public static final SnakeMoveStrategy GROUND_MOMENTUM = new NamedMoveStrategy(
            "G M",
            (context) -> scoreAndPick(context, 3, 0, 0, 2, 1, 0));
    public static final SnakeMoveStrategy FOOD = new NamedMoveStrategy(
            "F",
            (context) -> scoreAndPick(context, 0, 3, 0, 1, 1, 0));
    public static final SnakeMoveStrategy FOOD_MOMENTUM = new NamedMoveStrategy(
            "F M",
            (context) -> scoreAndPick(context, 0, 3, 0, 2, 1, 0));
    public static final SnakeMoveStrategy FOOD_VERTICAL_CLUSTERED = new NamedMoveStrategy(
            "F V C+",
            (context) -> scoreAndPick(context, 0, 3, 1, 1, 0, 2));
    public static final SnakeMoveStrategy FOOD_HORIZONTAL_CLUSTERED = new NamedMoveStrategy(
            "F H C+",
            (context) -> scoreAndPick(context, 0, 3, -1, 1, 0, 2));
    public static final SnakeMoveStrategy FOOD_VERTICAL_SPREAD = new NamedMoveStrategy(
            "F V C-",
            (context) -> scoreAndPick(context, 0, 3, 1, 1, 0, -2));
    public static final SnakeMoveStrategy FOOD_HORIZONTAL_SPREAD = new NamedMoveStrategy(
            "F H C-",
            (context) -> scoreAndPick(context, 0, 3, -1, 1, 0, -2));
    public static final SnakeMoveStrategy FOOD_VERTICAL_MOMENTUM = new NamedMoveStrategy(
            "F V M",
            (context) -> scoreAndPick(context, 0, 3, 1, 2, 1, 0));
    public static final SnakeMoveStrategy FOOD_HORIZONTAL_MOMENTUM = new NamedMoveStrategy(
            "F H M",
            (context) -> scoreAndPick(context, 0, 3, -1, 2, 1, 0));
    public static final SnakeMoveStrategy FOOD_MOMENTUM_CLUSTERED = new NamedMoveStrategy(
            "F M C+",
            (context) -> scoreAndPick(context, 0, 3, 0, 2, 1, 2));
    public static final SnakeMoveStrategy FOOD_MOMENTUM_SPREAD = new NamedMoveStrategy(
            "F M C-",
            (context) -> scoreAndPick(context, 0, 3, 0, 2, 1, -2));

    /**
     * Private constructor to prevent instantiation.
     */
    private SnakeMoveStrategies() {
    }

    public static List<SnakeMoveStrategy> strategiesForConfig() {
        List<SnakeMoveStrategy> strategies = new ArrayList<>();
        strategies.add(MOMENTUM);
        strategies.add(VERTICAL_MOMENTUM);
        strategies.add(HORIZONTAL_MOMENTUM);
        strategies.add(GROUND);
        strategies.add(GROUND_MOMENTUM);
        strategies.add(FOOD);
        strategies.add(FOOD_MOMENTUM);
        strategies.add(FOOD_VERTICAL_CLUSTERED);
        strategies.add(FOOD_HORIZONTAL_CLUSTERED);
        strategies.add(FOOD_VERTICAL_SPREAD);
        strategies.add(FOOD_HORIZONTAL_SPREAD);
        strategies.add(FOOD_VERTICAL_MOMENTUM);
        strategies.add(FOOD_HORIZONTAL_MOMENTUM);
        strategies.add(FOOD_MOMENTUM_CLUSTERED);
        strategies.add(FOOD_MOMENTUM_SPREAD);
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
