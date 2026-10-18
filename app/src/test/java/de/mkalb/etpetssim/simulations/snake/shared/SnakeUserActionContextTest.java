package de.mkalb.etpetssim.simulations.snake.shared;

import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

final class SnakeUserActionContextTest {

    @Test
    void testAddSnakeStoresStrategy() {
        SnakeUserActionContext.AddSnake context = new SnakeUserActionContext.AddSnake(SnakeMoveStrategies.MOMENTUM);

        assertSame(SnakeMoveStrategies.MOMENTUM, context.strategy());
    }

}
