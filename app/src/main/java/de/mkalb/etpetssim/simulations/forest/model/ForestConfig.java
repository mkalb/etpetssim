package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

public record ForestConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        double treeDensity,
        double treeGrowthProbability,
        double lightningIgnitionProbability,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {}
