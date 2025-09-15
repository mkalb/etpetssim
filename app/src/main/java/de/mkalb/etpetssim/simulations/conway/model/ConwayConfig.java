package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;

public record ConwayConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        double alivePercent,
        NeighborhoodMode neighborhoodMode,
        ConwayTransitionRules transitionRules)
        implements SimulationConfig {}
