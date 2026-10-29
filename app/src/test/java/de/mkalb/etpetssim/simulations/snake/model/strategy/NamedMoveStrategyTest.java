package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

final class NamedMoveStrategyTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testDecideMove() {
        Optional<MoveDecision> decision = Optional.of(
                new TestMoveDecision(new GridCoordinate(1, 2), CompassDirection.N, false));
        AtomicInteger invocationCount = new AtomicInteger();
        NamedMoveStrategy strategy = new NamedMoveStrategy("Test", _ -> {
            invocationCount.incrementAndGet();
            return decision;
        });

        Optional<MoveDecision> result = strategy.decideMove(null);

        assertAll(
                () -> assertSame(decision, result),
                () -> assertEquals(1, invocationCount.get())
        );
    }

    private record TestMoveDecision(
            GridCoordinate targetCoordinate,
            CompassDirection direction,
            boolean isFoodTarget) implements MoveDecision {
    }

}
