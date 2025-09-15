package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;

public record LabConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        ColorMode colorMode,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    public enum ColorMode {
        COLOR, BLACK_WHITE
    }

}
