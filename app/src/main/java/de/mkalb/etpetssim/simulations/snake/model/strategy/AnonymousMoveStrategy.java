package de.mkalb.etpetssim.simulations.snake.model.strategy;

@FunctionalInterface
interface AnonymousMoveStrategy extends SnakeMoveStrategy {

    @Override
    default String name() {
        return toString();
    }

}