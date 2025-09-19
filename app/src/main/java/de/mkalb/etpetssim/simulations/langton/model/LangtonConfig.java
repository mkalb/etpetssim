package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;

public record LangtonConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        NeighborhoodMode neighborhoodMode,
        LangtonMovementRules langtonMovementRules)
        implements SimulationConfig {}
