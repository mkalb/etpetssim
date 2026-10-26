package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.snake.model.strategy.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SnakeHeadTest {

    private static final SnakeMoveStrategy STRATEGY = SnakeMoveStrategies.strategies().getFirst();

    private static SnakeHead createSnakeHead() {
        return new SnakeHead(1, STRATEGY, 2, 5);
    }

    @Test
    void testToDisplayString() {
        String strategyName = STRATEGY.displayName();
        String arrow = CompassDirection.N.arrow();
        SnakeHead newSnake = createSnakeHead();
        SnakeHead growingSnake = createSnakeHead();
        growingSnake.move(new GridCoordinate(1, 2), CompassDirection.N, 0, 10);
        SnakeHead grownSnake = createSnakeHead();
        grownSnake.move(new GridCoordinate(1, 3), CompassDirection.N, 0, 10);
        grownSnake.move(new GridCoordinate(1, 2), CompassDirection.N, 0, 10);
        grownSnake.move(new GridCoordinate(1, 1), CompassDirection.N, 0, 10);
        SnakeHead deadSnake = createSnakeHead();
        deadSnake.move(new GridCoordinate(1, 2), CompassDirection.N, 0, 10);
        deadSnake.die();

        assertAll(
                () -> assertEquals("[SNAKE #1 + *5 L=0+2 P=0 " + strategyName + "]",
                        newSnake.toDisplayString()),
                () -> assertEquals("[SNAKE #1 " + arrow + " *5 L=1+1 P=10 " + strategyName + "]",
                        growingSnake.toDisplayString()),
                () -> assertEquals("[SNAKE #1 " + arrow + " *5 L=2 P=30 " + strategyName + "]",
                        grownSnake.toDisplayString()),
                () -> assertEquals("[SNAKE #1 " + arrow + " †1 L=1+1 P=10 " + strategyName + "]",
                        deadSnake.toDisplayString())
        );
    }

}
