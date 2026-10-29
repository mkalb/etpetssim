package de.mkalb.etpetssim.simulations.snake.model.strategy;

import java.util.Optional;

/**
 * Strategy contract for selecting the next snake move.
 */
public sealed interface SnakeMoveStrategy permits AnonymousMoveStrategy, NamedMoveStrategy {

    /**
     * Computes the next move decision for the given simulation context.
     *
     * @param context current snake move context
     * @return next move decision, or empty when no valid move is found
     */
    Optional<MoveDecision> decideMove(MoveContext context);

    /**
     * Returns the display name of this strategy.
     *
     * @return strategy display name
     */
    String displayName();

}
