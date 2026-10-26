package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.snake.model.*;
import de.mkalb.etpetssim.simulations.snake.model.entity.*;
import de.mkalb.etpetssim.simulations.snake.shared.SnakeDeathMode;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class SnakeMoveStrategiesTest {

    private static final GridCoordinate HEAD_COORDINATE = new GridCoordinate(5, 5);
    private static final SnakeConfig CONFIG = new SnakeConfig(
            CellShape.SQUARE,
            SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
            SnakeConstraints.GRID_WIDTH_DEFAULT,
            SnakeConstraints.GRID_HEIGHT_DEFAULT,
            SnakeConstraints.CELL_EDGE_LENGTH_DEFAULT,
            SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
            1L,
            0,
            0,
            0,
            SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
            NeighborhoodMode.EDGES_ONLY,
            SnakeDeathMode.PERMADEATH,
            SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
            SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
            SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT
    );
    private static final GridStructure STRUCTURE = CONFIG.createGridStructure();

    private static SnakeMoveStrategy strategy(String displayName) {
        return SnakeMoveStrategies.strategies()
                                  .stream()
                                  .filter(strategy -> strategy.displayName().equals(displayName))
                                  .findFirst()
                                  .orElseThrow();
    }

    private static SnakeHead createSnakeHead(SnakeMoveStrategy strategy) {
        return new SnakeHead(1, strategy, 2, 0);
    }

    private static CellNeighborWithEdgeBehavior neighbor(CompassDirection direction) {
        return CellNeighborhoods.cellNeighborsWithEdgeBehavior(HEAD_COORDINATE, CONFIG.neighborhoodMode(), STRUCTURE)
                                .values()
                                .stream()
                                .flatMap(List::stream)
                                .filter(neighbor -> neighbor.direction() == direction)
                                .findFirst()
                                .orElseThrow();
    }

    private static MoveContext createContext(SnakeHead snakeHead,
                                             List<CompassDirection> groundDirections,
                                             List<CompassDirection> foodDirections,
                                             long seed) {
        return new MoveContext(
                snakeHead,
                HEAD_COORDINATE,
                new SparseGridModel<>(STRUCTURE, TerrainConstant.GROUND),
                groundDirections.stream().map(SnakeMoveStrategiesTest::neighbor).toList(),
                foodDirections.stream().map(SnakeMoveStrategiesTest::neighbor).toList(),
                STRUCTURE,
                CellNeighborhoods.SQUARE_EDGES_DIRECTION_RING,
                CONFIG,
                new Random(seed));
    }

    private static MoveDecision decideMove(String displayName,
                                           List<CompassDirection> groundDirections,
                                           List<CompassDirection> foodDirections) {
        SnakeMoveStrategy strategy = strategy(displayName);
        return strategy.decideMove(createContext(createSnakeHead(strategy), groundDirections, foodDirections, 1L))
                       .orElseThrow();
    }

    private static MoveDecision decideMoveHeadingNorthWithTwoSegments(String displayName) {
        SnakeMoveStrategy strategy = strategy(displayName);
        SnakeHead snakeHead = createSnakeHead(strategy);
        snakeHead.move(new GridCoordinate(6, 6), CompassDirection.W, 0, 0);
        snakeHead.move(new GridCoordinate(5, 6), CompassDirection.N, 0, 0);
        MoveContext context = createContext(snakeHead, List.of(CompassDirection.E, CompassDirection.W), List.of(), 1L);
        return strategy.decideMove(context).orElseThrow();
    }

    @Test
    void testStrategies() {
        List<SnakeMoveStrategy> strategies = SnakeMoveStrategies.strategies();

        assertAll(
                () -> assertEquals(List.of("M", "V M", "H M", "G", "G M", "F", "F M", "F V C+", "F H C+", "F V C-",
                                "F H C-", "F V M", "F H M", "F M C+", "F M C-"),
                        strategies.stream().map(SnakeMoveStrategy::displayName).toList()),
                () -> assertSame(strategies, SnakeMoveStrategies.strategies()),
                () -> assertThrows(UnsupportedOperationException.class, () -> strategies.add(strategies.getFirst()))
        );
    }

    @Test
    void testScoreAndPickReturnsEmptyWithoutNeighbors() {
        SnakeMoveStrategy strategy = strategy("M");

        assertTrue(strategy.decideMove(createContext(createSnakeHead(strategy), List.of(), List.of(), 1L)).isEmpty());
    }

    @Test
    void testScoreAndPickReturnsOnlyNeighbor() {
        MoveDecision decision = decideMove("G", List.of(), List.of(CompassDirection.E));

        assertAll(
                () -> assertEquals(CompassDirection.E, decision.direction()),
                () -> assertEquals(new GridCoordinate(6, 5), decision.targetCoordinate()),
                () -> assertTrue(decision.isFoodTarget())
        );
    }

    @Test
    void testScoreAndPickPrefersFoodForFood() {
        MoveDecision decision = decideMove("F", List.of(CompassDirection.N), List.of(CompassDirection.S));

        assertAll(
                () -> assertEquals(CompassDirection.S, decision.direction()),
                () -> assertTrue(decision.isFoodTarget())
        );
    }

    @Test
    void testScoreAndPickPrefersGroundForGround() {
        MoveDecision decision = decideMove("G", List.of(CompassDirection.N), List.of(CompassDirection.S));

        assertAll(
                () -> assertEquals(CompassDirection.N, decision.direction()),
                () -> assertFalse(decision.isFoodTarget())
        );
    }

    @Test
    void testScoreAndPickPrefersStraightForMomentum() {
        SnakeMoveStrategy strategy = strategy("M");
        SnakeHead snakeHead = createSnakeHead(strategy);
        snakeHead.move(new GridCoordinate(4, 5), CompassDirection.E, 0, 0);
        MoveContext context = createContext(snakeHead,
                List.of(CompassDirection.N, CompassDirection.E, CompassDirection.S), List.of(), 1L);

        assertEquals(CompassDirection.E, strategy.decideMove(context).orElseThrow().direction());
    }

    @Test
    void testScoreAndPickPrefersVerticalForVerticalMomentum() {
        MoveDecision decision = decideMove("V M", List.of(CompassDirection.E, CompassDirection.N), List.of());

        assertEquals(CompassDirection.N, decision.direction());
    }

    @Test
    void testScoreAndPickPrefersHorizontalForHorizontalMomentum() {
        MoveDecision decision = decideMove("H M", List.of(CompassDirection.N, CompassDirection.E), List.of());

        assertEquals(CompassDirection.E, decision.direction());
    }

    @Test
    void testScoreAndPickPrefersAdjacentSegmentForClustered() {
        MoveDecision decision = decideMoveHeadingNorthWithTwoSegments("F V C+");

        assertEquals(CompassDirection.E, decision.direction());
    }

    @Test
    void testScoreAndPickAvoidsAdjacentSegmentForSpread() {
        MoveDecision decision = decideMoveHeadingNorthWithTwoSegments("F V C-");

        assertEquals(CompassDirection.W, decision.direction());
    }

    @Test
    void testPickRandomTopScoredMoveChoosesAmongTopScoredMoves() {
        SnakeMoveStrategy strategy = strategy("F");
        List<CompassDirection> foodDirections = List.of(CompassDirection.N, CompassDirection.E, CompassDirection.S);

        Set<CompassDirection> chosenDirections = LongStream.range(0, 50)
                                                           .mapToObj(seed -> createContext(createSnakeHead(strategy),
                                                                   List.of(CompassDirection.W), foodDirections, seed))
                                                           .map(context -> strategy.decideMove(context).orElseThrow())
                                                           .map(MoveDecision::direction)
                                                           .collect(Collectors.toSet());

        assertEquals(Set.copyOf(foodDirections), chosenDirections);
    }

}
