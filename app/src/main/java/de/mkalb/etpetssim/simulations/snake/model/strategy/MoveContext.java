package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import de.mkalb.etpetssim.simulations.snake.model.entity.*;

import java.util.*;

/**
 * Immutable decision context passed to snake move strategies.
 *
 * @param snakeHead             the moving snake head entity
 * @param headCoordinate        the current coordinate of the snake head
 * @param model                 the snake grid model to inspect
 * @param groundNeighbors       neighboring ground candidates with resolved edge behavior
 * @param foodNeighbors         neighboring food candidates with resolved edge behavior
 * @param structure             the active grid structure
 * @param neighborDirectionRing the ordered direction ring used for local strategy decisions
 * @param config                the active snake configuration
 * @param random                the random source for non-deterministic choices; intentionally shared rather than
 *                              defensively copied, because {@link Random} is not cloneable and strategies must
 *                              advance the same sequence as the caller
 */
public record MoveContext(
        SnakeHead snakeHead,
        GridCoordinate headCoordinate,
        ReadableGridModel<SnakeEntity> model,
        List<CellNeighborWithEdgeBehavior> groundNeighbors,
        List<CellNeighborWithEdgeBehavior> foodNeighbors,
        GridStructure structure,
        List<CompassDirection> neighborDirectionRing,
        SnakeConfig config,
        Random random) {}
