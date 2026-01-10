package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;

public interface MoveDecision {

    GridCoordinate targetCoordinate();

    CompassDirection direction();

    boolean isFoodTarget();

}
