package de.mkalb.etpetssim.simulations.snake.model.strategy;

/**
 * Helper contract for creating snake move strategies from lambdas.
 *
 * <p>Its {@link #displayName()} default only keeps the interface functional; {@link NamedMoveStrategy}
 * wraps every instance and supplies the real display name.
 */
@FunctionalInterface
non-sealed interface AnonymousMoveStrategy extends SnakeMoveStrategy {

    @Override
    default String displayName() {
        return toString();
    }

}
