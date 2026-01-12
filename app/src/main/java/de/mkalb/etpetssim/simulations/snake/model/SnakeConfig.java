package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

public record SnakeConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        // Initialization
        int verticalWalls,
        int foodCells,
        int snakes,
        int initialPendingGrowth,
        // Rules
        SnakeDeathMode deathMode,
        int growthPerFood,
        int basePointsPerFood,
        double segmentLengthMultiplier,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {
}

