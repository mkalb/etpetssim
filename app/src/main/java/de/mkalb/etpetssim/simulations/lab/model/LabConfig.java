package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;

public record LabConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        RenderingMode renderingMode,
        ColorMode colorMode,
        StrokeMode strokeMode)
        implements SimulationConfig {

    public GridTopology getGridTopology() {
        return new GridTopology(cellShape, gridEdgeBehavior);
    }

    public GridSize getGridSize() {
        return new GridSize(gridWidth, gridHeight);
    }

    public boolean isValid() {
        return !GridSize.isInvalidSize(gridWidth)
                && !GridSize.isInvalidSize(gridHeight)
                && GridStructure.isValid(getGridTopology(), getGridSize())
                && (cellEdgeLength > 0);
    }

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
