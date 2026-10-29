package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighbor;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Catalog of the predefined snake move strategies.
 *
 * <p>Each strategy scores the ground and food neighbors of the snake head with fixed weights and picks one of the
 * top-scored moves at random. Its display name combines these abbreviations:
 * <ul>
 *   <li>{@code G}: prefers ground</li>
 *   <li>{@code F}: prefers food</li>
 *   <li>{@code M}: momentum; prefers moving straight on or making a small turn</li>
 *   <li>{@code V}, {@code H}: prefers vertical or horizontal moves</li>
 *   <li>{@code C+}, {@code C-}: prefers or avoids cells next to its own segments (clustered or spread)</li>
 * </ul>
 */
public final class SnakeMoveStrategies {

    private static final SnakeMoveStrategy MOMENTUM = createStrategy(
            "M", new MoveWeights(0, 1, 0, 3, 1, 0));
    private static final SnakeMoveStrategy VERTICAL_MOMENTUM = createStrategy(
            "V M", new MoveWeights(0, 1, 2, 3, 1, 0));
    private static final SnakeMoveStrategy HORIZONTAL_MOMENTUM = createStrategy(
            "H M", new MoveWeights(0, 1, -2, 3, 1, 0));
    private static final SnakeMoveStrategy GROUND = createStrategy(
            "G", new MoveWeights(3, 0, 0, 1, 1, 0));
    private static final SnakeMoveStrategy GROUND_MOMENTUM = createStrategy(
            "G M", new MoveWeights(3, 0, 0, 2, 1, 0));
    private static final SnakeMoveStrategy FOOD = createStrategy(
            "F", new MoveWeights(0, 3, 0, 1, 1, 0));
    private static final SnakeMoveStrategy FOOD_MOMENTUM = createStrategy(
            "F M", new MoveWeights(0, 3, 0, 2, 1, 0));
    private static final SnakeMoveStrategy FOOD_VERTICAL_CLUSTERED = createStrategy(
            "F V C+", new MoveWeights(0, 3, 1, 1, 0, 2));
    private static final SnakeMoveStrategy FOOD_HORIZONTAL_CLUSTERED = createStrategy(
            "F H C+", new MoveWeights(0, 3, -1, 1, 0, 2));
    private static final SnakeMoveStrategy FOOD_VERTICAL_SPREAD = createStrategy(
            "F V C-", new MoveWeights(0, 3, 1, 1, 0, -2));
    private static final SnakeMoveStrategy FOOD_HORIZONTAL_SPREAD = createStrategy(
            "F H C-", new MoveWeights(0, 3, -1, 1, 0, -2));
    private static final SnakeMoveStrategy FOOD_VERTICAL_MOMENTUM = createStrategy(
            "F V M", new MoveWeights(0, 3, 1, 2, 1, 0));
    private static final SnakeMoveStrategy FOOD_HORIZONTAL_MOMENTUM = createStrategy(
            "F H M", new MoveWeights(0, 3, -1, 2, 1, 0));
    private static final SnakeMoveStrategy FOOD_MOMENTUM_CLUSTERED = createStrategy(
            "F M C+", new MoveWeights(0, 3, 0, 2, 1, 2));
    private static final SnakeMoveStrategy FOOD_MOMENTUM_SPREAD = createStrategy(
            "F M C-", new MoveWeights(0, 3, 0, 2, 1, -2));

    private static final List<SnakeMoveStrategy> STRATEGIES = List.of(
            MOMENTUM,
            VERTICAL_MOMENTUM,
            HORIZONTAL_MOMENTUM,
            GROUND,
            GROUND_MOMENTUM,
            FOOD,
            FOOD_MOMENTUM,
            FOOD_VERTICAL_CLUSTERED,
            FOOD_HORIZONTAL_CLUSTERED,
            FOOD_VERTICAL_SPREAD,
            FOOD_HORIZONTAL_SPREAD,
            FOOD_VERTICAL_MOMENTUM,
            FOOD_HORIZONTAL_MOMENTUM,
            FOOD_MOMENTUM_CLUSTERED,
            FOOD_MOMENTUM_SPREAD);

    private SnakeMoveStrategies() {
    }

    /**
     * Returns all predefined move strategies in a stable order.
     *
     * <p>The simulation assigns them to the initial snakes in this order, starting again with the first one.
     *
     * @return an unmodifiable list of all predefined move strategies
     */
    public static List<SnakeMoveStrategy> strategies() {
        return STRATEGIES;
    }

    private static SnakeMoveStrategy createStrategy(String displayName, MoveWeights weights) {
        return new NamedMoveStrategy(displayName, context -> scoreAndPick(context, weights));
    }

    private static Optional<MoveDecision> scoreAndPick(MoveContext context, MoveWeights weights) {
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

        CompassDirection snakeDirection = context.snakeHead().direction().orElse(null);
        List<GridCoordinate> segments = ((weights.adjacentSegment() != 0) && (context.snakeHead().segmentCount() > 0))
                ? context.snakeHead().currentSegments()
                : List.of();

        List<ScoredMove> moves = new ArrayList<>(movesCapacity);
        scoreNeighbors(context, context.groundNeighbors(), false, weights.ground(), weights, snakeDirection, segments,
                moves);
        scoreNeighbors(context, context.foodNeighbors(), true, weights.food(), weights, snakeDirection, segments,
                moves);
        return pickRandomTopScoredMove(moves, context.random());
    }

    private static void scoreNeighbors(MoveContext context,
                                       List<CellNeighborWithEdgeBehavior> neighbors,
                                       boolean isFoodTarget,
                                       int baseWeight,
                                       MoveWeights weights,
                                       @Nullable CompassDirection snakeDirection,
                                       List<GridCoordinate> segments,
                                       List<ScoredMove> moves) {
        for (var neighbor : neighbors) {
            int score = baseWeight;

            if ((weights.vertical() != 0) && isVertical(neighbor.direction())) {
                score += weights.vertical();
            }

            // Bias for moving straight on or making only a small turn
            if (snakeDirection != null) {
                int distance = CompassDirection.distanceOnRing(snakeDirection, neighbor.direction(),
                        context.neighborDirectionRing());
                if (distance == 0) {
                    score += weights.straight();
                } else if (distance == 1) {
                    score += weights.smallTurn();
                }
                // distance >= 2 -> no momentum bonus
            }

            if ((weights.adjacentSegment() != 0) && !segments.isEmpty()
                    && hasNeighborIn(neighbor.mappedNeighborCoordinate(), segments,
                    context.config().neighborhoodMode(), context.structure())) {
                score += weights.adjacentSegment();
            }

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

    private static ScoredMove createScoredMove(int score,
                                               CellNeighborWithEdgeBehavior neighbor,
                                               boolean isFoodTarget) {
        return new ScoredMove(score,
                neighbor.mappedNeighborCoordinate(),
                neighbor.direction(),
                isFoodTarget);
    }

    private static Optional<MoveDecision> pickRandomTopScoredMove(List<ScoredMove> moves, Random random) {
        // The early returns without a tie consume no random value and keep the random sequence of a seed stable.
        if (moves.isEmpty()) {
            return Optional.empty();
        }
        if (moves.size() == 1) {
            return Optional.of(moves.getFirst());
        }

        int topScore = moves.stream().mapToInt(ScoredMove::score).max().getAsInt();
        List<ScoredMove> topScoredMoves = new ArrayList<>(moves.size());
        for (var entry : moves) {
            if (entry.score() == topScore) {
                topScoredMoves.add(entry);
            }
        }

        if (topScoredMoves.size() == 1) {
            return Optional.of(topScoredMoves.getFirst());
        }
        return Optional.of(topScoredMoves.get(random.nextInt(topScoredMoves.size())));
    }

    /**
     * Weights that a strategy adds to the score of a move.
     *
     * @param ground          the base score of a move to a ground cell
     * @param food            the base score of a move to a food cell
     * @param vertical        the bonus of a vertical move ({@code N} or {@code S}); negative to prefer other moves
     * @param straight        the bonus of a move in the current direction of the snake
     * @param smallTurn       the bonus of a move next to the current direction on the direction ring
     * @param adjacentSegment the bonus of a move to a cell next to an own segment; negative to avoid such cells
     */
    private record MoveWeights(
            int ground,
            int food,
            int vertical,
            int straight,
            int smallTurn,
            int adjacentSegment) {
    }

    private record ScoredMove(
            int score,
            GridCoordinate targetCoordinate,
            CompassDirection direction,
            boolean isFoodTarget) implements MoveDecision {
    }

}
