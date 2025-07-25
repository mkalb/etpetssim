package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.engine.*;

public record LabConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength) {

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

}
