package de.mkalb.etpetssim.simulations.snake.model.strategy;

import java.util.*;

record NamedMoveStrategy(
        String name,
        AnonymousMoveStrategy strategy) implements SnakeMoveStrategy {

    @Override
    public Optional<MoveDecision> decideMove(MoveContext context) {
        return strategy.decideMove(context);
    }

    @Override
    public String toString() {
        return name;
    }

}
