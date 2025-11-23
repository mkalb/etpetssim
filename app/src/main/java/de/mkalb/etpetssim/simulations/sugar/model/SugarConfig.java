package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

public record SugarConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        // Initialization
        double sugarPercent,
        double agentPercent,
        int maxSugarAmount,
        int agentInitialEnergy,
        // Rules
        int sugarRegenerationRate,
        int agentMetabolismRate,
        int agentVisionRange,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {
}
