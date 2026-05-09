package de.mkalb.etpetssim.simulations.snake.model.strategy;

/**
 * Helper contract for anonymous functional snake move strategies.
 */
@FunctionalInterface
interface AnonymousMoveStrategy extends SnakeMoveStrategy {

    @Override
    default String name() {
        return toString();
    }

}