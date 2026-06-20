package de.mkalb.etpetssim.simulations.snake.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SnakeUserActionContextTest {

    @Test
    void testPermittedSubclasses() {
        assertArrayEquals(
                new Class<?>[]{
                        SnakeUserActionContext.FixedAction.class,
                        SnakeUserActionContext.AddSnake.class
                },
                SnakeUserActionContext.class.getPermittedSubclasses());
    }

    @Test
    void testFixedActionDeclarationOrder() {
        assertArrayEquals(
                new SnakeUserActionContext.FixedAction[]{
                        SnakeUserActionContext.FixedAction.ADD_WALL,
                        SnakeUserActionContext.FixedAction.REMOVE_WALL,
                        SnakeUserActionContext.FixedAction.ADD_FOOD,
                        SnakeUserActionContext.FixedAction.REMOVE_FOOD,
                        SnakeUserActionContext.FixedAction.REMOVE_SNAKE
                },
                SnakeUserActionContext.FixedAction.values());
    }

    @Test
    void testAddSnakeStoresStrategy() {
        SnakeUserActionContext.AddSnake context = new SnakeUserActionContext.AddSnake(SnakeMoveStrategies.MOMENTUM);

        assertAll(
                () -> assertInstanceOf(SimulationUserActionContext.class, context),
                () -> assertSame(SnakeMoveStrategies.MOMENTUM, context.strategy())
        );
    }

}
