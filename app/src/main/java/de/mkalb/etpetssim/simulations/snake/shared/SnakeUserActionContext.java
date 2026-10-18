package de.mkalb.etpetssim.simulations.snake.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategy;

/**
 * Identifies which Snake edit action should be applied.
 */
@SuppressWarnings("MarkerInterface")
public sealed interface SnakeUserActionContext extends SimulationUserActionContext
        permits SnakeUserActionContext.FixedAction, SnakeUserActionContext.AddSnake {

    /**
     * Fixed actions that do not require additional parameters.
     */
    enum FixedAction implements SnakeUserActionContext {
        ADD_WALL,
        REMOVE_WALL,
        ADD_FOOD,
        REMOVE_FOOD,
        REMOVE_SNAKE
    }

    /**
     * Parameterized context for creating a snake with a selected move strategy.
     *
     * @param strategy selected move strategy
     */
    record AddSnake(SnakeMoveStrategy strategy) implements SnakeUserActionContext {
    }

}
