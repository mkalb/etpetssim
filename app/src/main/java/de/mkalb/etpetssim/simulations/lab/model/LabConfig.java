package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;

public record LabConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        long seed,
        RenderingMode renderingMode,
        ColorMode colorMode,
        StrokeMode strokeMode,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    public enum RenderingMode {
        SHAPE, CIRCLE
    }

    public enum ColorMode {
        COLOR, BLACK_WHITE
    }

    public enum StrokeMode {
        NONE, CENTERED
    }

}
