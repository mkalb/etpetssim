package de.mkalb.etpetssim.simulations.snake.shared;

import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SnakeUserActionContextTest {

    @Test
    void testAddSnakeRecord() {
        SnakeUserActionContext.AddSnake context = new SnakeUserActionContext.AddSnake(SnakeMoveStrategies.MOMENTUM);

        assertAll(
                () -> assertSame(SnakeMoveStrategies.MOMENTUM, context.strategy()),
                () -> assertTrue(context.toString().contains("strategy=" + SnakeMoveStrategies.MOMENTUM)));
    }

}
