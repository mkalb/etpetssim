package de.mkalb.etpetssim.simulations.snake.model.strategy;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;

import java.util.*;

public record MoveContext(
        SnakeHead snakeHead,
        GridCoordinate headCoordinate,
        ReadableGridModel<SnakeEntity> model,
        List<CellNeighborWithEdgeBehavior> groundNeighbors,
        List<CellNeighborWithEdgeBehavior> foodNeighbors,
        GridStructure structure,
        SnakeConfig config,
        Random random) {}
