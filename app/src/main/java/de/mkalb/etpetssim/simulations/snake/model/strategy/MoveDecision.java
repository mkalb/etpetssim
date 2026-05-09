package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;

/**
 * Describes the outcome of a snake move-strategy decision.
 */
public interface MoveDecision {

    /**
     * Returns the target coordinate for the next move.
     *
     * @return target coordinate
     */
    GridCoordinate targetCoordinate();

    /**
     * Returns the heading associated with the selected move.
     *
     * @return move direction
     */
    CompassDirection direction();

    /**
     * Indicates whether the target coordinate contains food.
     *
     * @return {@code true} when the selected target is food
     */
    boolean isFoodTarget();

}
