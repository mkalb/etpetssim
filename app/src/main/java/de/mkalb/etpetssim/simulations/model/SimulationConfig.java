package de.mkalb.etpetssim.simulations.model;

import de.mkalb.etpetssim.engine.*;

public interface SimulationConfig {

    CellShape cellShape();

    GridEdgeBehavior gridEdgeBehavior();

    int gridWidth();

    int gridHeight();

    double cellEdgeLength();

    CellDisplayMode cellDisplayMode();

    long seed();

    default GridTopology createGridTopology() {
        return new GridTopology(cellShape(), gridEdgeBehavior());
    }

    default GridSize createGridSize() {
        return new GridSize(gridWidth(), gridHeight());
    }

    default GridStructure createGridStructure() {
        return new GridStructure(createGridTopology(), createGridSize());
    }

    default int calculateCellCount() {
        return gridWidth() * gridHeight();
    }

    default boolean isValid() {
        return !GridSize.isInvalidSize(gridWidth())
                && !GridSize.isInvalidSize(gridHeight())
                && GridStructure.isValid(createGridTopology(), createGridSize())
                && (cellEdgeLength() > 0);
    }

}
