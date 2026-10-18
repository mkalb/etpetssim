package de.mkalb.etpetssim.simulations.snake.model.strategy;

import java.util.*;

record NamedMoveStrategy(
        String displayName,
        AnonymousMoveStrategy strategy) implements SnakeMoveStrategy {

    @Override
    public Optional<MoveDecision> decideMove(MoveContext context) {
        return strategy.decideMove(context);
    }

    @Override
    public String toString() {
        return displayName;
    }

}
