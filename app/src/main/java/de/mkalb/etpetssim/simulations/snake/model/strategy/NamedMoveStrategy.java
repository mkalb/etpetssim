package de.mkalb.etpetssim.simulations.snake.model.strategy;

import java.util.Optional;

/**
 * Move strategy that gives an anonymous strategy a display name.
 *
 * @param displayName the human-readable name of the strategy
 * @param strategy    the strategy that decides the moves
 */
record NamedMoveStrategy(
        String displayName,
        AnonymousMoveStrategy strategy) implements SnakeMoveStrategy {

    @Override
    public Optional<MoveDecision> decideMove(MoveContext context) {
        return strategy.decideMove(context);
    }

}
